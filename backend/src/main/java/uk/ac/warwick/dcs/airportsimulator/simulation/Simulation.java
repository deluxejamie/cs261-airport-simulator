package uk.ac.warwick.dcs.airportsimulator.simulation;

import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLog;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.events.EventSchedular;
import uk.ac.warwick.dcs.airportsimulator.priorityqueue.HoldingPattern;
import uk.ac.warwick.dcs.airportsimulator.priorityqueue.TakeOffQueue;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Simulation {

    private final List<Runway> runways;
    private final HoldingPattern holdingPattern;
    private final TakeOffQueue takeOffQueue;

    private int simTime;

    private final EventSchedular eventSchedular;
    private final EventLog eventLog;

    public Simulation(List<Runway> runways) {
        this.runways = new ArrayList<>(Objects.requireNonNull(runways, "runways"));
        this.holdingPattern = new HoldingPattern(0);
        this.takeOffQueue = new TakeOffQueue();
        this.simTime = 0;

        this.eventSchedular = new EventSchedular();
        this.eventLog = new EventLog();
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

        boolean noPendingEvents = eventSchedular.isEmpty();
        return queuesEmpty && !anyOccupied && noPendingEvents;
    }

    public List<EventLogEntry> getEventLog(int offset, int count) {
        return eventLog.getEvents(offset, count);
    }

    public SimulationResult run() {
        SimulationResult result = new SimulationResult();

        // run any events scheduled at t=0
        eventSchedular.step(simTime);

        int safetyCap = 1_000_000;
        while (!isFinished() && safetyCap-- > 0) {
            simTime += 1;
            eventSchedular.step(simTime);

            // Future work goes here.
        }

        result.finalizeAverages();
        return result;
    }

    // getters for other components (optional)
    public int getSimTime() { return simTime; }
    public void setSimTime(int simTime) { this.simTime = simTime; }

    public EventSchedular getEventSchedular() { return eventSchedular; }
    public EventLog getEventLogStore() { return eventLog; }

    public HoldingPattern getHoldingPattern() { return holdingPattern; }
    public TakeOffQueue getTakeOffQueue() { return takeOffQueue; }
    public List<Runway> getRunways() { return List.copyOf(runways); }
}