package uk.ac.warwick.dcs.airportsimulator.service;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;
import uk.ac.warwick.dcs.airportsimulator.simulation.AircraftOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a validated and parsed simulation configuration that is ready to be
 * converted into a {@code Simulation} instance.
 *
 * This class acts as an intermediate representation between frontend request DTOs
 * and the internal simulation setup process. It contains:
 *
 */
public class ParsedSimulationConfig {
    private final int maxDelayBeforeCancelled;
    private final int fuelThresholdBeforeRedirected;
    private final int timeTakenForTakeoff;
    private final int timeTakenForLanding;

    private final List<Runway> runways = new ArrayList<>();
    private final List<ParsedFlight> flights = new ArrayList<>();
    private final List<ParsedRunwayClosure> runwayClosures = new ArrayList<>();
    private final List<ParsedEmergencyEvent> emergencyEvents = new ArrayList<>();

    /**
     * Creates a parsed simulation configuration with global timing parameters.
     *
     * @param maxDelayBeforeCancelled maximum permitted delay before a departure is cancelled
     * @param fuelThresholdBeforeRedirected fuel threshold below which an arrival may be redirected
     * @param timeTakenForTakeoff time required for one takeoff operation
     * @param timeTakenForLanding time required for one landing operation
     */
    public ParsedSimulationConfig(int maxDelayBeforeCancelled,
                                  int fuelThresholdBeforeRedirected,
                                  int timeTakenForTakeoff,
                                  int timeTakenForLanding) {
        this.maxDelayBeforeCancelled = maxDelayBeforeCancelled;
        this.fuelThresholdBeforeRedirected = fuelThresholdBeforeRedirected;
        this.timeTakenForTakeoff = timeTakenForTakeoff;
        this.timeTakenForLanding = timeTakenForLanding;
    }

    /**
     * Returns the maximum delay allowed before a flight is cancelled.
     *
     * @return maximum cancellation delay
     */
    public int getMaxDelayBeforeCancelled() {
        return maxDelayBeforeCancelled;
    }

    /**
     * Returns the fuel threshold below which an arrival may be redirected.
     *
     * @return fuel threshold in minutes
     */
    public int getFuelThresholdBeforeRedirected() {
        return fuelThresholdBeforeRedirected;
    }

    /**
     * Returns the time required for a takeoff operation.
     *
     * @return takeoff duration
     */
    public int getTimeTakenForTakeoff() {
        return timeTakenForTakeoff;
    }

    /**
     * Returns the time required for a landing operation.
     *
     * @return landing duration
     */
    public int getTimeTakenForLanding() {
        return timeTakenForLanding;
    }

    /**
     * Returns the parsed list of runways.
     *
     * @return mutable list of runways
     */
    public List<Runway> getRunways() {
        return runways;
    }

    /**
     * Returns the parsed list of flights.
     *
     * @return mutable list of parsed flights
     */
    public List<ParsedFlight> getFlights() {
        return flights;
    }

    /**
     * Returns the parsed list of runway closure events.
     *
     * @return mutable list of parsed runway closures
     */
    public List<ParsedRunwayClosure> getRunwayClosures() {
        return runwayClosures;
    }

    /**
     * Returns the parsed list of aircraft emergency events.
     *
     * @return mutable list of parsed emergency events
     */
    public List<ParsedEmergencyEvent> getEmergencyEvents() {
        return emergencyEvents;
    }

    /**
     * Represents a parsed flight entry from the request configuration.
     *
     * Each flight includes the aircraft object, the operation type
     * (arrival or departure), the scheduled time, repeat interval,
     * end time for repetition, and a seed value.
     */
    public static class ParsedFlight {
        private final Aircraft aircraft;
        private final AircraftOp op;
        private final int scheduled;
        private final int interval;
        private final int end;
        private final long seed;

        /**
         * Creates a parsed flight definition.
         *
         * @param aircraft aircraft associated with the flight
         * @param op operation type
         * @param scheduled initial scheduled time
         * @param interval repeat interval; 0 if non-repeating
         * @param end end time for repetition
         * @param seed random seed associated with the flight
         */
        public ParsedFlight(Aircraft aircraft, AircraftOp op, int scheduled, int interval, int end, long seed) {
            this.aircraft = aircraft;
            this.op = op;
            this.scheduled = scheduled;
            this.interval = interval;
            this.end = end;
            this.seed = seed;
        }

        /**
         * Returns the aircraft for this parsed flight.
         *
         * @return aircraft instance
         */
        public Aircraft getAircraft() {
            return aircraft;
        }

        /**
         * Returns the aircraft operation type.
         *
         * @return operation type
         */
        public AircraftOp getOp() {
            return op;
        }

        /**
         * Returns the initial scheduled time.
         *
         * @return scheduled time
         */
        public int getScheduled() {
            return scheduled;
        }

        /**
         * Returns the repeat interval.
         *
         * @return interval, or 0 if non-repeating
         */
        public int getInterval() {
            return interval;
        }

        /**
         * Returns the repetition end time.
         *
         * @return end time
         */
        public int getEnd() {
            return end;
        }

        /**
         * Returns the seed used for this flight.
         *
         * @return random seed
         */
        public long getSeed() {
            return seed;
        }
    }

    /**
     * Represents a parsed runway closure or runway status change event.
     */
    public static class ParsedRunwayClosure {
        private final int runwayNumber;
        private final int scheduled;
        private final int interval;
        private final int end;
        private final RunwayStatus status;

        /**
         * Creates a parsed runway closure definition.
         *
         * @param runwayNumber affected runway number
         * @param scheduled initial scheduled time
         * @param interval repeat interval; 0 if non-repeating
         * @param end end time for repetition
         * @param status runway status to apply
         */
        public ParsedRunwayClosure(int runwayNumber, int scheduled, int interval, int end, RunwayStatus status) {
            this.runwayNumber = runwayNumber;
            this.scheduled = scheduled;
            this.interval = interval;
            this.end = end;
            this.status = status;
        }

        /**
         * Returns the affected runway number.
         *
         * @return runway number
         */
        public int getRunwayNumber() {
            return runwayNumber;
        }

        /**
         * Returns the initial scheduled time.
         *
         * @return scheduled time
         */
        public int getScheduled() {
            return scheduled;
        }

        /**
         * Returns the repeat interval.
         *
         * @return interval, or 0 if non-repeating
         */
        public int getInterval() {
            return interval;
        }

        /**
         * Returns the repetition end time.
         *
         * @return end time
         */
        public int getEnd() {
            return end;
        }

        /**
         * Returns the runway status applied by this event.
         *
         * @return runway status
         */
        public RunwayStatus getStatus() {
            return status;
        }
    }

    /**
     * Represents a parsed emergency event targeting a specific arrival aircraft.
     */
    public static class ParsedEmergencyEvent {
        private final String callsign;
        private final int scheduled;
        private final EmergencyStatus emergencyStatus;

        /**
         * Creates a parsed emergency event.
         *
         * @param callsign target aircraft callsign
         * @param scheduled time at which the emergency occurs
         * @param emergencyStatus emergency status to assign
         */
        public ParsedEmergencyEvent(String callsign, int scheduled, EmergencyStatus emergencyStatus) {
            this.callsign = callsign;
            this.scheduled = scheduled;
            this.emergencyStatus = emergencyStatus;
        }

        /**
         * Returns the target aircraft callsign.
         *
         * @return aircraft callsign
         */
        public String getCallsign() {
            return callsign;
        }

        /**
         * Returns the scheduled time of the emergency event.
         *
         * @return scheduled time
         */
        public int getScheduled() {
            return scheduled;
        }

        /**
         * Returns the emergency status to apply.
         *
         * @return emergency status
         */
        public EmergencyStatus getEmergencyStatus() {
            return emergencyStatus;
        }
    }
}