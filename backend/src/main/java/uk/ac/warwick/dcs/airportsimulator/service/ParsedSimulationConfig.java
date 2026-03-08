package uk.ac.warwick.dcs.airportsimulator.service;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;
import uk.ac.warwick.dcs.airportsimulator.simulation.AircraftOp;

import java.util.ArrayList;
import java.util.List;

public class ParsedSimulationConfig {
    private final int maxDelayBeforeCancelled;
    private final int fuelThresholdBeforeRedirected;
    private final int timeTakenForTakeoff;
    private final int timeTakenForLanding;

    private final List<Runway> runways = new ArrayList<>();
    private final List<ParsedFlight> flights = new ArrayList<>();
    private final List<ParsedRunwayClosure> runwayClosures = new ArrayList<>();
    private final List<ParsedEmergencyEvent> emergencyEvents = new ArrayList<>();

    public ParsedSimulationConfig(int maxDelayBeforeCancelled,
                                  int fuelThresholdBeforeRedirected,
                                  int timeTakenForTakeoff,
                                  int timeTakenForLanding) {
        this.maxDelayBeforeCancelled = maxDelayBeforeCancelled;
        this.fuelThresholdBeforeRedirected = fuelThresholdBeforeRedirected;
        this.timeTakenForTakeoff = timeTakenForTakeoff;
        this.timeTakenForLanding = timeTakenForLanding;
    }

    public int getMaxDelayBeforeCancelled() {
        return maxDelayBeforeCancelled;
    }

    public int getFuelThresholdBeforeRedirected() {
        return fuelThresholdBeforeRedirected;
    }

    public int getTimeTakenForTakeoff() {
        return timeTakenForTakeoff;
    }

    public int getTimeTakenForLanding() {
        return timeTakenForLanding;
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public List<ParsedFlight> getFlights() {
        return flights;
    }

    public List<ParsedRunwayClosure> getRunwayClosures() {
        return runwayClosures;
    }

    public List<ParsedEmergencyEvent> getEmergencyEvents() {
        return emergencyEvents;
    }

    public static class ParsedFlight {
        private final Aircraft aircraft;
        private final AircraftOp op;
        private final int scheduled;
        private final int interval;
        private final int end;
        private final long seed;

        public ParsedFlight(Aircraft aircraft, AircraftOp op, int scheduled, int interval, int end, long seed) {
            this.aircraft = aircraft;
            this.op = op;
            this.scheduled = scheduled;
            this.interval = interval;
            this.end = end;
            this.seed = seed;
        }

        public Aircraft getAircraft() {
            return aircraft;
        }

        public AircraftOp getOp() {
            return op;
        }

        public int getScheduled() {
            return scheduled;
        }

        public int getInterval() {
            return interval;
        }

        public int getEnd() {
            return end;
        }

        public long getSeed() {
            return seed;
        }
    }

    public static class ParsedRunwayClosure {
        private final int runwayNumber;
        private final int scheduled;
        private final int interval;
        private final int end;
        private final RunwayStatus status;

        public ParsedRunwayClosure(int runwayNumber, int scheduled, int interval, int end, RunwayStatus status) {
            this.runwayNumber = runwayNumber;
            this.scheduled = scheduled;
            this.interval = interval;
            this.end = end;
            this.status = status;
        }

        public int getRunwayNumber() {
            return runwayNumber;
        }

        public int getScheduled() {
            return scheduled;
        }

        public int getInterval() {
            return interval;
        }

        public int getEnd() {
            return end;
        }

        public RunwayStatus getStatus() {
            return status;
        }
    }

    public static class ParsedEmergencyEvent {
        private final String callsign;
        private final int scheduled;
        private final EmergencyStatus emergencyStatus;

        public ParsedEmergencyEvent(String callsign, int scheduled, EmergencyStatus emergencyStatus) {
            this.callsign = callsign;
            this.scheduled = scheduled;
            this.emergencyStatus = emergencyStatus;
        }

        public String getCallsign() {
            return callsign;
        }

        public int getScheduled() {
            return scheduled;
        }

        public EmergencyStatus getEmergencyStatus() {
            return emergencyStatus;
        }
    }
}