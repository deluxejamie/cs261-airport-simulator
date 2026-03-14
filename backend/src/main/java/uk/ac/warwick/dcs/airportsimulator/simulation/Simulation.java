package uk.ac.warwick.dcs.airportsimulator.simulation;

import uk.ac.warwick.dcs.airportsimulator.events.IEvent;
import uk.ac.warwick.dcs.airportsimulator.events.PlainEvent;
import uk.ac.warwick.dcs.airportsimulator.events.NormDistEvent;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventType;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * The simulation class represents the simulation
 */
public class Simulation {

    private final List<Runway> runways;
    private final HoldingPattern holdingPattern;
    private final TakeOffQueue takeOffQueue;
    private final Set<Aircraft> arrivalsEnteredSim = new HashSet<>();
    private final Map<Integer, Integer> runwayBusyUntil = new HashMap<>();

    private int simTime;

    private final EventSchedular eventSchedular;
    private final EventLog eventLog;

    private int maxDelayBeforeCancelled = 30;
    private int fuelThresholdBeforeRedirected = 10;
    private int timeTakenForTakeoff = 1;
    private int timeTakenForLanding = 1;

    private final Lock mutex = new ReentrantLock(true);

    /**
     * Constructs the simulation class with given runways
     *
     * @param runways runways for sim
     */
    public Simulation(List<Runway> runways) {
        this.runways = new ArrayList<>(Objects.requireNonNull(runways, "runways"));
        this.holdingPattern = new HoldingPattern(0); // 0 => unlimited
        this.takeOffQueue = new TakeOffQueue();
        this.simTime = 0;

        this.eventSchedular = new EventSchedular();
        this.eventLog = new EventLog();

        for (Runway runway : this.runways) {
            runwayBusyUntil.put(runway.getRunwayNumber(), Integer.MIN_VALUE);
        }
    }

    /**
     * Checks if the Simulation is finished
     *
     * @return whether the sim is finished
     */
    public boolean isFinished() {
        final boolean queuesEmpty = holdingPattern.isEmpty() && takeOffQueue.isEmpty();

        boolean anyOccupied = false;
        for (Runway r : runways) {
            if (r.getOccupied() != null) {
                anyOccupied = true;
                break;
            }
        }

        final boolean noPendingEvents = eventSchedular.isEmpty();
        return queuesEmpty && !anyOccupied && noPendingEvents;
    }

    /**
     * Returns section of event log from offset and count
     *
     * @param offset offset to read event log from
     * @param count  amount to read from event log
     * @return the section of event log requested
     */
    public List<EventLogEntry> getEventLog(int offset, int count) {
        return eventLog.getEvents(offset, count);
    }


    /**
     * Releases runways whose occupancy time has elapsed.
     */
    private void releaseCompletedRunways() {
        for (Runway runway : runways) {
            Integer busyUntil = runwayBusyUntil.get(runway.getRunwayNumber());
            if (busyUntil != null && simTime >= busyUntil && runway.getOccupied() != null) {
                runway.setOccupied(null);
            }
        }
    }

    /**
     * Returns true if the runway is available for a new operation at the current time.
     *
     * @param runway runway to check
     * @return whether the runway is available now
     */
    private boolean isRunwayAvailableNow(Runway runway) {
        final Integer busyUntil = runwayBusyUntil.get(runway.getRunwayNumber());
        if (busyUntil == null) {
            return runway.getOccupied() == null;
        }
        return runway.getOccupied() == null && simTime >= busyUntil;
    }

    /**
     * Returns true if the runway can currently handle a landing.
     *
     * @param runway runway to check
     * @return whether landing can be handled
     */
    private boolean canHandleLanding(Runway runway) {
        return runway.getStatus() == RunwayStatus.AVAILABLE
                && (runway.getMode() == RunwayMode.LANDING || runway.getMode() == RunwayMode.MIXED_MODE)
                && isRunwayAvailableNow(runway);
    }

    /**
     * Returns true if the runway can currently handle a takeoff.
     *
     * @param runway runway to check
     * @return whether takeoff can be handled
     */
    private boolean canHandleTakeoff(Runway runway) {
        return runway.getStatus() == RunwayStatus.AVAILABLE
                && (runway.getMode() == RunwayMode.TAKE_OFF || runway.getMode() == RunwayMode.MIXED_MODE)
                && isRunwayAvailableNow(runway);
    }

    private int startingTime() {
        final int firstInTakeOffQueue = takeOffQueue.isEmpty() ? 0 : takeOffQueue.peekNextAircraft().getScheduledTime();
        final int firstInHoldingPattern = holdingPattern.isEmpty() || holdingPattern.peekNextAircraft() == null ? 0 : holdingPattern.peekNextAircraft().getScheduledTime();
        final int firstEventScheduled = eventSchedular.isEmpty() ? 0 : eventSchedular.nextEventTime();

        final int minOfQueues = Math.min(firstInTakeOffQueue, firstInHoldingPattern);
        return Math.min(minOfQueues, firstEventScheduled);
    }

    public SimulationResult run() {
        final int MAX_TAKEOFF_WAIT_MIN = maxDelayBeforeCancelled;
        final SimulationResult result = new SimulationResult();

        // Set correct starting simTime
        simTime = startingTime();
        eventSchedular.step(simTime);

        int safetyCap = 1_000_000;
        while (!isFinished() && safetyCap-- > 0) {
            mutex.lock();

            releaseCompletedRunways();

            result.recordHoldQueueSize(holdingPattern.size(), simTime);
            result.recordTakeoffQueueSize(takeOffQueue.size(), simTime);

            // diversion: remove fuel-critical aircraft from holding pattern
            while (true) {
                final Aircraft fuelCritical = holdingPattern.pollIfFuelCritical(simTime, fuelThresholdBeforeRedirected);
                if (fuelCritical == null) {
                    break;
                }

                result.recordDiversion();

                final HashMap<String, Object> attr = new HashMap<>();
                attr.put("callsign", fuelCritical.getCallSign());
                attr.put("reason", "FUEL_CRITICAL");
                logEvent(EventType.DIVERSION_EVENT, simTime, attr);
            }

            // cancellation: remove aircraft that waited too long in takeoff queue
            while (true) {
                final Aircraft nextTake = takeOffQueue.peekNextAircraft();
                if (nextTake == null) {
                    break;
                }

                final int waited = simTime - nextTake.getScheduledTime();
                if (waited < MAX_TAKEOFF_WAIT_MIN) {
                    break;
                }

                takeOffQueue.getNextAircraft();
                result.recordCancellation();

                final HashMap<String, Object> attr = new HashMap<>();
                attr.put("callsign", nextTake.getCallSign());
                attr.put("reason", "MAX_WAIT_EXCEEDED");
                attr.put("waitedMinutes", waited);
                logEvent(EventType.CANCELLATION_EVENT, simTime, attr);
            }

            // runway assignment
            for (Runway r : runways) {
                Aircraft chosen = null;
                boolean landing = false;

                switch (r.getMode()) {
                    case LANDING -> {
                        if (!canHandleLanding(r)) {
                            continue;
                        }
                        chosen = holdingPattern.peekNextAircraft();
                        landing = true;
                    }
                    case TAKE_OFF -> {
                        if (!canHandleTakeoff(r)) {
                            continue;
                        }
                        chosen = takeOffQueue.peekNextAircraft();
                        landing = false;
                    }
                    case MIXED_MODE -> {
                        if (r.getStatus() != RunwayStatus.AVAILABLE || !isRunwayAvailableNow(r)) {
                            continue;
                        }

                        final Aircraft nextHold = holdingPattern.peekNextAircraft();
                        final Aircraft nextTake = takeOffQueue.peekNextAircraft();

                        if (nextHold == null && nextTake == null) {
                            chosen = null;
                        } else if (nextHold == null) {
                            chosen = nextTake;
                            landing = false;
                        } else if (nextTake == null) {
                            chosen = nextHold;
                            landing = true;
                        } else {
                            // PRIORITISE EMERGENCY ARRIVALS
                            if (nextHold.getEmergencyStatus() != EmergencyStatus.NONE) {
                                chosen = nextHold;
                                landing = true;
                            } else {

                                final int holdSlack = Math.max(
                                        0,
                                        nextHold.getFuelRemaining(simTime) - fuelThresholdBeforeRedirected
                                );
                            final int takeSlack = Math.max(
                                    0,
                                    MAX_TAKEOFF_WAIT_MIN - (simTime - nextTake.getScheduledTime())
                            ); 

                            if (holdSlack <= takeSlack) {
                                chosen = nextHold;
                                landing = true;
                            } else {
                                chosen = nextTake;
                                landing = false;
                            }
                            }
                        }
                    }
                }

                if (chosen == null) {
                    continue;
                }

                // remove from queue
                if (landing) {
                    holdingPattern.getNextAircraft();
                } else {
                    takeOffQueue.getNextAircraft();
                }

                // occupy runway for configured duration
                r.setOccupied(chosen);
                int operationDuration = landing ? timeTakenForLanding : timeTakenForTakeoff;
                if (operationDuration < 1) {
                    operationDuration = 1;
                }
                runwayBusyUntil.put(r.getRunwayNumber(), simTime + operationDuration);

                final HashMap<String, Object> attr = new HashMap<>();
                attr.put("callsign", chosen.getCallSign());
                attr.put("runwayNumber", r.getRunwayNumber());

                final int delay = Math.max(simTime - chosen.getScheduledTime(), 0);

                if (landing) {
                    final int holdTime = Math.max(0, simTime - chosen.getScheduledTime());
                    result.recordHoldTime(holdTime);
                    result.recordArrivalDelay(delay);

                    attr.put("holdMinutes", holdTime);
                    attr.put("arrivalDelay", delay);
                    attr.put("runwayOccupiedMinutes", operationDuration);
                    logEvent(EventType.LANDING_EVENT, simTime, attr);
                } else {
                    final int waitTime = Math.max(0, simTime - chosen.getScheduledTime());
                    result.recordTakeoffWait(waitTime);
                    result.recordDepartureDelay(delay);

                    attr.put("waitMinutes", waitTime);
                    attr.put("departureDelay", delay);
                    attr.put("runwayOccupiedMinutes", operationDuration);
                    logEvent(EventType.TAKEOFF_EVENT, simTime, attr);
                }
            }

            simTime += 1;
            eventSchedular.step(simTime);

            mutex.unlock();
        }

        result.finalizeAverages();
        return result;
    }

    /**
     * Gets event schedular
     *
     * @return the event schedular
     */
    public EventSchedular getEventSchedular() {
        return eventSchedular;
    }

    /**
     * Gets the event log store
     *
     * @return event log store
     */
    public EventLog getEventLogStore() {
        return eventLog;
    }

    /**
     * Gets the holding pattern
     *
     * @return the holding pattern
     */
    public HoldingPattern getHoldingPattern() {
        return holdingPattern;
    }

    /**
     * Gets the takeoff queue
     *
     * @return the takeoff queue
     */
    public TakeOffQueue getTakeOffQueue() {
        return takeOffQueue;
    }

    /**
     * Gets the runways
     *
     * @return the runways
     */
    public List<Runway> getRunways() {
        return List.copyOf(runways);
    }

    /**
     * Adds an aircraft to a simulation
     *
     * @param a         aircraft to add
     * @param scheduled time the aircraft is scheduled
     * @param interval  interval if event is recurring
     * @param end       time to end if the event is recurring
     * @param seed      the seed for the aircraft
     * @param op        the aircraft operation
     */
    public void addAircraft(Aircraft a, int scheduled, int interval, int end, long seed, AircraftOp op) {
        Objects.requireNonNull(a, "aircraft");
        Objects.requireNonNull(op, "op");

        final Consumer<Integer> action = (currSchedule) -> {
            a.updateWith(currSchedule, simTime);

            final HashMap<String, Object> attr = new HashMap<>();
            attr.put("callsign", a.getCallSign());
            attr.put("op", op.toString());

            if (op == AircraftOp.ARRIVAL) {
                holdingPattern.addAircraft(a);
                arrivalsEnteredSim.add(a);
                logEvent(EventType.HOLDING_EVENT, simTime, attr);

            } else {
                takeOffQueue.addAircraft(a);
                logEvent(EventType.HOLDING_EVENT, simTime, attr);
            }
        };

        IEvent event;
        if (interval > 0) {
            event = new NormDistEvent(scheduled, interval, end, seed, action);
        } else {
            event = new NormDistEvent(scheduled, seed, action);
        }
        eventSchedular.addEvent(event);
    }

    /**
     * Adds a runway operation change event
     *
     * @param runwayNumber the runway to modify
     * @param scheduled    the time the event should happen
     * @param interval     if recurring at what interval
     * @param end          if recurring the end time
     * @param mode         the mode to change runway to
     */
    public void addRunwayOperationChange(int runwayNumber, int scheduled, int interval, int end, RunwayMode mode) {
        Objects.requireNonNull(mode, "mode");

        Runnable action = () -> {
            final Runway r = getRunwayByNumber(runwayNumber);
            r.setMode(mode);

            final HashMap<String, Object> attr = new HashMap<>();
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

    /**
     * Adds an aircraft emergency
     *
     * @param a               the aircraft to add the emergency for
     * @param scheduled       the time to add the emergency
     * @param interval        if recurring the interval for the event
     * @param end             if recurring the end time
     * @param emergencyStatus the emergency status to set
     */
    public void addAircraftEmergency(Aircraft a, int scheduled, int interval, int end, EmergencyStatus emergencyStatus) {
        Objects.requireNonNull(a, "aircraft");
        Objects.requireNonNull(emergencyStatus, "emergencyStatus");

        Runnable action = () -> {
            final boolean entered = arrivalsEnteredSim.contains(a);
            final boolean inHolding = holdingPattern.containsAircraft(a);
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

            final HashMap<String, Object> attr = new HashMap<>();
            attr.put("callsign", a.getCallSign());
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

    /**
     * Adds a runway status change event
     *
     * @param runwayNumber the runway to change
     * @param scheduled    the time to change
     * @param interval     if recurring the interval
     * @param end          if recurring the end
     * @param status       if recurring the status
     */
    public void addRunwayStatusChange(int runwayNumber, int scheduled, int interval, int end, RunwayStatus status) {
        Objects.requireNonNull(status, "status");

        Runnable action = () -> {
            final Runway r = getRunwayByNumber(runwayNumber);
            r.setStatus(status);

            final HashMap<String, Object> attr = new HashMap<>();
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

    /**
     * Gets the runway from runway number
     *
     * @param runwayNumber the runway number
     * @return the runway
     */
    private Runway getRunwayByNumber(int runwayNumber) {
        for (Runway r : runways) {
            if (r.getRunwayNumber() == runwayNumber) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid runway number: " + runwayNumber);
    }

    /**
     * Logs an event
     *
     * @param type      the event type
     * @param timestamp the event timestamp
     * @param attr      the event attributes
     */
    private void logEvent(EventType type, int timestamp, HashMap<String, Object> attr) {
        eventLog.addEntry(new EventLogEntry(type, timestamp, attr));
    }

    /**
     * Returns the maximum delay allowed before a flight is cancelled.
     *
     * @return the maximum delay before cancellation
     */
    public int getMaxDelayBeforeCancelled() {
        return maxDelayBeforeCancelled;
    }

    /**
     * Sets the maximum delay allowed before a flight is cancelled.
     *
     * @param maxDelayBeforeCancelled the maximum delay before cancellation
     */
    public void setMaxDelayBeforeCancelled(int maxDelayBeforeCancelled) {
        this.maxDelayBeforeCancelled = maxDelayBeforeCancelled;
    }

    /**
     * Returns the fuel threshold below which an aircraft may be redirected.
     *
     * @return the fuel threshold before redirection
     */
    public int getFuelThresholdBeforeRedirected() {
        return fuelThresholdBeforeRedirected;
    }

    /**
     * Sets the fuel threshold below which an aircraft may be redirected.
     *
     * @param fuelThresholdBeforeRedirected the fuel threshold before redirection
     */
    public void setFuelThresholdBeforeRedirected(int fuelThresholdBeforeRedirected) {
        this.fuelThresholdBeforeRedirected = fuelThresholdBeforeRedirected;
    }

    /**
     * Returns the time required for a takeoff operation.
     *
     * @return the time taken for takeoff
     */
    public int getTimeTakenForTakeoff() {
        return timeTakenForTakeoff;
    }

    /**
     * Sets the time required for a takeoff operation.
     *
     * @param timeTakenForTakeoff the time taken for takeoff
     */
    public void setTimeTakenForTakeoff(int timeTakenForTakeoff) {
        this.timeTakenForTakeoff = timeTakenForTakeoff;
    }

    /**
     * Returns the time required for a landing operation.
     *
     * @return the time taken for landing
     */
    public int getTimeTakenForLanding() {
        return timeTakenForLanding;
    }

    /**
     * Sets the time required for a landing operation.
     *
     * @param timeTakenForLanding the time taken for landing
     */
    public void setTimeTakenForLanding(int timeTakenForLanding) {
        this.timeTakenForLanding = timeTakenForLanding;
    }

    /**
     * Gets the mutex
     *
     * @return get mutex for simulation
     */
    public Lock getMutex() {
        return mutex;
    }

    /**
     * Get Event Log Count
     *
     * @return number of the events in the log
     */
    public long getNumOfEventsInLog() {
        return eventLog.getNumOfEvents();
    }
}