package uk.ac.warwick.dcs.airportsimulator.simulation;

import uk.ac.warwick.dcs.airportsimulator.events.IEvent;
import uk.ac.warwick.dcs.airportsimulator.events.PlainEvent;
import uk.ac.warwick.dcs.airportsimulator.events.NormDistEvent;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventType;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLog;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.events.EventSchedular;
import uk.ac.warwick.dcs.airportsimulator.priorityqueue.HoldingPattern;
import uk.ac.warwick.dcs.airportsimulator.priorityqueue.TakeOffQueue;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Simulation {

    private final List<Runway> runways;
    private final HoldingPattern holdingPattern;
    private final TakeOffQueue takeOffQueue;
    private final Set<Aircraft> arrivalsEnteredSim = new HashSet<>();

    private int simTime;

    private final EventSchedular eventSchedular;
    private final EventLog eventLog;

    public Simulation(List<Runway> runways) {
        this.runways = new ArrayList<>(Objects.requireNonNull(runways, "runways"));
        this.holdingPattern = new HoldingPattern(0); // 0 => unlimited
        this.takeOffQueue = new TakeOffQueue();
        this.simTime = 0;

        this.eventSchedular = new EventSchedular();
        this.eventLog = new EventLog(); // requires public ctor
    }

    public boolean isFinished() {
        boolean queuesEmpty = holdingPattern.size() == 0 && takeOffQueue.size() == 0;

        boolean anyOccupied = false;
        for (Runway r : runways) {
            if (r.getOccupied() != null) {
                anyOccupied = true;
                break;
            }
        }

        boolean noPendingEvents = eventSchedular.isEmpty(); // add helper in EventSchedular
        return queuesEmpty && !anyOccupied && noPendingEvents;
    }

    public List<EventLogEntry> getEventLog(int offset, int count) {
        return eventLog.getEvents(offset, count);
    }

    // getters for Simulator
    public int getSimTime() { return simTime; }

    /**
     * TODO: REMOVE THIS FUNCTION
     * This is for testing as certain tickets needed have not been merged
     * Note: this is not an example of how to implement, this is missing important
     *       behaviour but suffices for the tests.
     * @param dt change in sim time
     */
    public void stepTestTillScrumMerged(int dt)
    {
        for (int i = 0; i < dt; ++i)
        {
            simTime += 1;
            eventSchedular.step(simTime);
            final Aircraft landing = holdingPattern.peekNextAircraft();
            if (landing != null && landing.getScheduledTime() >= simTime)
            {
                holdingPattern.getNextAircraft();
            }

            final Aircraft takeoff = takeOffQueue.peekNextAircraft();
            if (takeoff != null && takeoff.getScheduledTime() >= simTime)
            {
                holdingPattern.getNextAircraft();
            }
        }

    }

    public EventSchedular getEventSchedular() { return eventSchedular; }
    public EventLog getEventLogStore() { return eventLog; }

    public HoldingPattern getHoldingPattern() { return holdingPattern; }
    public TakeOffQueue getTakeOffQueue() { return takeOffQueue; }
    public List<Runway> getRunways() { return List.copyOf(runways); }

    public void addAircraft(Aircraft a, int scheduled, int interval, int end, AircraftOp op) {
        Objects.requireNonNull(a, "aircraft");
        Objects.requireNonNull(op, "op");

        Runnable action = () -> {
            HashMap<String, Object> attr = new HashMap<>();
            attr.put("callSign", a.getCallSign());
            attr.put("op", op.toString());

            if (op == AircraftOp.ARRIVAL) {
                holdingPattern.addAircraft(a);
                arrivalsEnteredSim.add(a);
                logEvent(EventType.HOLDING_EVENT, simTime, attr);
            } else {
                takeOffQueue.addAircraft(a);
                logEvent(EventType.TAKEOFF_EVENT, simTime, attr);
            }
        };

        long seed = a.getCallSign().hashCode();
        IEvent event;
        if (interval > 0) {
            event = new NormDistEvent(scheduled, interval, end, seed, action);
        } else {
            event = new NormDistEvent(scheduled, seed, action);
        }
        eventSchedular.addEvent(event);
    }

    public void addRunwayOperationChange(int runwayNumber, int scheduled, int interval, int end, RunwayMode mode) {
        Objects.requireNonNull(mode, "mode");

        Runnable action = () -> {
            Runway r = getRunwayByNumber(runwayNumber);
            r.setMode(mode);

            HashMap<String, Object> attr = new HashMap<>();
            attr.put("runwayNumber", runwayNumber);
            attr.put("mode", mode.toString());
            logEvent(EventType.RUNWAY_MODE_EVENT, simTime, attr);
        };

        IEvent event;
        if (interval > 0) {
            event = new PlainEvent(scheduled, interval, end, action);
        } else {
            event = new PlainEvent(scheduled, action);
        }
        eventSchedular.addEvent(event);
    }

    public void addAircraftEmergency(Aircraft a, int scheduled, int interval, int end, EmergencyStatus emergencyStatus) {
        Objects.requireNonNull(a, "aircraft");
        Objects.requireNonNull(emergencyStatus, "emergencyStatus");

        Runnable action = () -> {
            boolean entered = arrivalsEnteredSim.contains(a);
            boolean inHolding = holdingPattern.containsAircraft(a);
            if (entered && !inHolding) {
                return;
            }
            if (inHolding) {
                holdingPattern.removeAircraft(a);
            }
            a.setEmergencyStatus(emergencyStatus);

            if (inHolding) {
                holdingPattern.addAircraft(a);
            }
            HashMap<String, Object> attr = new HashMap<>();
            attr.put("callSign", a.getCallSign());
            attr.put("emergencyStatus", emergencyStatus.toString());

            logEvent(EventType.EMERGENCY_EVENT, simTime, attr);
        };

        IEvent event;
        if (interval > 0) {
            event = new PlainEvent(scheduled, interval, end, action);
        } else {
            event = new PlainEvent(scheduled, action);
        }

        eventSchedular.addEvent(event);
    }

    public void addRunwayStatusChange(int runwayNumber, int scheduled, int interval, int end, RunwayStatus status) {
        Objects.requireNonNull(status, "status");

        Runnable action = () -> {
            Runway r = getRunwayByNumber(runwayNumber);
            r.setStatus(status);

            HashMap<String, Object> attr = new HashMap<>();
            attr.put("runwayNumber", runwayNumber);
            attr.put("status", status.toString());
            logEvent(EventType.RUNWAY_STATUS_EVENT, simTime, attr);
        };

        IEvent event;
        if (interval > 0) {
            event = new PlainEvent(scheduled, interval, end, action);
        } else {
            event = new PlainEvent(scheduled, action);
        }
        eventSchedular.addEvent(event);
    }

    private Runway getRunwayByNumber(int runwayNumber) {
        for (Runway r : runways) {
            if (r.getRunwayNumber() == runwayNumber) return r;
        }
        throw new IllegalArgumentException("Invalid runway number: "+runwayNumber);
    }

    private void logEvent(EventType type, int timestamp, HashMap<String, Object> attr) {
        eventLog.addEntry(new EventLogEntry(type, timestamp, attr));
    }
}