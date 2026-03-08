package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.dto.AdvancedConfigDto;
import uk.ac.warwick.dcs.airportsimulator.dto.FrontendFlightDto;
import uk.ac.warwick.dcs.airportsimulator.dto.FrontendHazardDto;
import uk.ac.warwick.dcs.airportsimulator.dto.FrontendRunwayDto;
import uk.ac.warwick.dcs.airportsimulator.dto.RepeatingDto;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;
import uk.ac.warwick.dcs.airportsimulator.simulation.AircraftOp;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class SimulationConfigParser {

    private static final int DEFAULT_MAX_DELAY_BEFORE_CANCELLED = 30;
    private static final int DEFAULT_FUEL_THRESHOLD_BEFORE_REDIRECTED = 10;
    private static final int DEFAULT_TIME_TAKEN_FOR_TAKEOFF = 1;
    private static final int DEFAULT_TIME_TAKEN_FOR_LANDING = 1;

    public ParsedSimulationConfig parse(SimulationRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Simulation request must not be null.");
        }

        AdvancedConfigDto advancedConfig = request.getAdvancedConfig();

        int maxDelayBeforeCancelled = advancedConfig == null || advancedConfig.getMaxDelayBeforeCancelled() == null
                ? DEFAULT_MAX_DELAY_BEFORE_CANCELLED
                : advancedConfig.getMaxDelayBeforeCancelled();

        int fuelThresholdBeforeRedirected = advancedConfig == null || advancedConfig.getFuelThresholdBeforeRedirected() == null
                ? DEFAULT_FUEL_THRESHOLD_BEFORE_REDIRECTED
                : advancedConfig.getFuelThresholdBeforeRedirected();

        int timeTakenForTakeoff = advancedConfig == null || advancedConfig.getTimeTakenForTakeoff() == null
                ? DEFAULT_TIME_TAKEN_FOR_TAKEOFF
                : advancedConfig.getTimeTakenForTakeoff();

        int timeTakenForLanding = advancedConfig == null || advancedConfig.getTimeTakenForLanding() == null
                ? DEFAULT_TIME_TAKEN_FOR_LANDING
                : advancedConfig.getTimeTakenForLanding();

        validateNonNegative(maxDelayBeforeCancelled, "advancedConfig.maxDelayBeforeCancelled");
        validateNonNegative(fuelThresholdBeforeRedirected, "advancedConfig.fuelThresholdBeforeRedirected");
        validatePositive(timeTakenForTakeoff, "advancedConfig.timeTakenForTakeoff");
        validatePositive(timeTakenForLanding, "advancedConfig.timeTakenForLanding");

        ParsedSimulationConfig parsed = new ParsedSimulationConfig(
                maxDelayBeforeCancelled,
                fuelThresholdBeforeRedirected,
                timeTakenForTakeoff,
                timeTakenForLanding
        );

        parseRunways(request, parsed);
        parseFlights(request, parsed);
        parseHazards(request, parsed);

        return parsed;
    }

    private void parseRunways(SimulationRequestDto request, ParsedSimulationConfig parsed) {
        if (request.getRunways() == null || request.getRunways().isEmpty()) {
            throw new IllegalArgumentException("At least one runway must be provided.");
        }

        if (request.getRunways().size() > 10) {
            throw new IllegalArgumentException("At most 10 runways may be configured.");
        }

        Set<Integer> seenIds = new HashSet<>();

        for (FrontendRunwayDto config : request.getRunways()) {
            requireNonNull(config, "runway");
            requireNonNull(config.getId(), "runway.id");
            requireNonBlank(config.getType(), "runway.type");

            if (!seenIds.add(config.getId())) {
                throw new IllegalArgumentException("Duplicate runway id: " + config.getId());
            }

            Runway runway = new Runway(
                    config.getId(),
                    0.0,
                    0.0,
                    parseRunwayMode(config.getType()),
                    RunwayStatus.AVAILABLE,
                    null
            );

            parsed.getRunways().add(runway);
        }
    }

    private void parseFlights(SimulationRequestDto request, ParsedSimulationConfig parsed) {
        if (request.getFlights() == null) {
            return;
        }

        Set<String> seenCallsigns = new HashSet<>();

        for (FrontendFlightDto flight : request.getFlights()) {
            requireNonNull(flight, "flight");
            requireNonBlank(flight.getType(), "flight.type");
            requireNonBlank(flight.getCallsign(), "flight.callsign");

            if (!seenCallsigns.add(flight.getCallsign())) {
                throw new IllegalArgumentException("Duplicate callsign: " + flight.getCallsign());
            }

            requireSeedValid(flight.getSeed(), "flight.seed");

            String type = normalize(flight.getType());

            if ("ARRIVAL".equals(type)) {
                parseArrivalFlight(flight, parsed);
            } else if ("DEPARTURE".equals(type)) {
                parseDepartureFlight(flight, parsed);
            } else {
                throw new IllegalArgumentException("Invalid flight.type: " + flight.getType());
            }
        }
    }

    private void parseArrivalFlight(FrontendFlightDto flight, ParsedSimulationConfig parsed) {
        requireNonNull(flight.getExpectedArrivalTime(), "arrival.expected_arrival_time");
        requireNonBlank(flight.getEmergencyStatus(), "arrival.emergency_status");
        requireNonNull(flight.getRemainingFuelMins(), "arrival.remaining_fuel_mins");

        validateNonNegative(flight.getExpectedArrivalTime(), "arrival.expected_arrival_time");
        validateNonNegative(flight.getRemainingFuelMins(), "arrival.remaining_fuel_mins");

        RepeatingDto repeating = flight.getRepeating();
        int scheduled = flight.getExpectedArrivalTime();
        int interval = repeating == null || repeating.getPeriod() == null ? 0 : repeating.getPeriod();
        int end = repeating == null || repeating.getEnd() == null ? scheduled : repeating.getEnd();

        validateRecurring(scheduled, interval, end, "arrival.repeating");

        Aircraft aircraft = new Aircraft(
                flight.getCallsign(),
                "UNKNOWN",
                "UNKNOWN",
                scheduled,
                0,
                0,
                flight.getRemainingFuelMins(),
                parseEmergencyStatus(flight.getEmergencyStatus()),
                0
        );

        parsed.getFlights().add(
                new ParsedSimulationConfig.ParsedFlight(
                        aircraft,
                        AircraftOp.ARRIVAL,
                        scheduled,
                        interval,
                        end
                )
        );
    }

    private void parseDepartureFlight(FrontendFlightDto flight, ParsedSimulationConfig parsed) {
        requireNonNull(flight.getExpectedDepartureTime(), "departure.expected_departure_time");
        validateNonNegative(flight.getExpectedDepartureTime(), "departure.expected_departure_time");

        RepeatingDto repeating = flight.getRepeating();
        int scheduled = flight.getExpectedDepartureTime();
        int interval = repeating == null || repeating.getPeriod() == null ? 0 : repeating.getPeriod();
        int end = repeating == null || repeating.getEnd() == null ? scheduled : repeating.getEnd();

        validateRecurring(scheduled, interval, end, "departure.repeating");

        Aircraft aircraft = new Aircraft(
                flight.getCallsign(),
                "UNKNOWN",
                "UNKNOWN",
                scheduled,
                0,
                0,
                0,
                EmergencyStatus.NONE,
                0
        );

        parsed.getFlights().add(
                new ParsedSimulationConfig.ParsedFlight(
                        aircraft,
                        AircraftOp.DEPARTURE,
                        scheduled,
                        interval,
                        end
                )
        );
    }

    private void parseHazards(SimulationRequestDto request, ParsedSimulationConfig parsed) {
        if (request.getHazards() == null) {
            return;
        }

        Set<Integer> validRunways = new HashSet<>();
        for (Runway runway : parsed.getRunways()) {
            validRunways.add(runway.getRunwayNumber());
        }

        Set<String> arrivalCallsigns = new HashSet<>();
        for (ParsedSimulationConfig.ParsedFlight flight : parsed.getFlights()) {
            if (flight.getOp() == AircraftOp.ARRIVAL) {
                arrivalCallsigns.add(flight.getAircraft().getCallSign());
            }
        }

        for (FrontendHazardDto hazard : request.getHazards()) {
            requireNonNull(hazard, "hazard");
            requireNonBlank(hazard.getType(), "hazard.type");

            String type = normalize(hazard.getType());

            if ("RUNWAY_CLOSURE".equals(type)) {
                parseRunwayClosure(hazard, parsed, validRunways);
            } else if ("EMERGENCY_EVENT".equals(type)) {
                parseEmergencyEvent(hazard, parsed, arrivalCallsigns);
            } else {
                throw new IllegalArgumentException("Invalid hazard.type: " + hazard.getType());
            }
        }
    }

    private void parseRunwayClosure(FrontendHazardDto hazard,
                                    ParsedSimulationConfig parsed,
                                    Set<Integer> validRunways) {
        requireNonNull(hazard.getAffectedRunway(), "hazard.affected_runway");
        requireNonBlank(hazard.getClosureMode(), "hazard.closure_mode");
        requireNonNull(hazard.getDurationMins(), "hazard.duration_mins");

        if (!validRunways.contains(hazard.getAffectedRunway())) {
            throw new IllegalArgumentException("Unknown affected_runway: " + hazard.getAffectedRunway());
        }

        validatePositive(hazard.getDurationMins(), "hazard.duration_mins");

        int scheduled = 0;
        int interval = 0;
        int end = scheduled + hazard.getDurationMins();

        if (hazard.getTime() != null) {
            scheduled = hazard.getTime();
            end = scheduled + hazard.getDurationMins();
        }

        if (hazard.getRepeating() != null) {
            RepeatingDto repeating = hazard.getRepeating();
            interval = repeating.getPeriod() == null ? 0 : repeating.getPeriod();
            end = repeating.getEnd() == null ? scheduled + hazard.getDurationMins() : repeating.getEnd();
        }

        validateRecurring(scheduled, interval, end, "hazard.repeating");

        parsed.getRunwayClosures().add(
                new ParsedSimulationConfig.ParsedRunwayClosure(
                        hazard.getAffectedRunway(),
                        scheduled,
                        interval,
                        end,
                        parseRunwayStatus(hazard.getClosureMode())
                )
        );
    }

    private void parseEmergencyEvent(FrontendHazardDto hazard,
                                     ParsedSimulationConfig parsed,
                                     Set<String> arrivalCallsigns) {
        requireNonBlank(hazard.getTargetArrivalCallsign(), "hazard.target_arrival_callsign");
        requireNonNull(hazard.getTime(), "hazard.time");
        validateNonNegative(hazard.getTime(), "hazard.time");

        if (!arrivalCallsigns.contains(hazard.getTargetArrivalCallsign())) {
            throw new IllegalArgumentException("Unknown target_arrival_callsign: " + hazard.getTargetArrivalCallsign());
        }

        parsed.getEmergencyEvents().add(
                new ParsedSimulationConfig.ParsedEmergencyEvent(
                        hazard.getTargetArrivalCallsign(),
                        hazard.getTime(),
                        EmergencyStatus.PASSENGER_HEALTH
                )
        );
    }

    private RunwayMode parseRunwayMode(String value) {
        return switch (normalize(value)) {
            case "TAKEOFF", "TAKE_OFF" -> RunwayMode.TAKE_OFF;
            case "LANDING" -> RunwayMode.LANDING;
            case "MIXED_MODE", "MIXED" -> RunwayMode.MIXED_MODE;
            default -> throw new IllegalArgumentException("Invalid runway.type: " + value);
        };
    }

    private RunwayStatus parseRunwayStatus(String value) {
        return switch (normalize(value)) {
            case "SNOW_CLEARANCE" -> RunwayStatus.SNOW_CLEARANCE;
            case "RUNWAY_INSPECTION" -> RunwayStatus.RUNWAY_INSPECTION;
            case "EQUIPMENT_FAILURE" -> RunwayStatus.EQUIPMENT_FAILURE;
            default -> throw new IllegalArgumentException("Invalid hazard.closure_mode: " + value);
        };
    }

    private EmergencyStatus parseEmergencyStatus(String value) {
        return switch (normalize(value)) {
            case "NONE" -> EmergencyStatus.NONE;
            case "PASSENGER_HEALTH" -> EmergencyStatus.PASSENGER_HEALTH;
            case "MECH_FAIL", "MECHANICAL_FAILURE", "MECHANICAL" -> EmergencyStatus.MECH_FAIL;
            default -> throw new IllegalArgumentException("Invalid arrival.emergency_status: " + value);
        };
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    private void validateRecurring(int scheduled, int interval, int end, String field) {
        validateNonNegative(scheduled, field + ".scheduled");
        validateNonNegative(interval, field + ".period");
        validateNonNegative(end, field + ".end");

        if (interval == 0 && end < scheduled) {
            throw new IllegalArgumentException(field + ".end must be >= scheduled.");
        }

        if (interval > 0 && end < scheduled) {
            throw new IllegalArgumentException(field + ".end must be >= scheduled when period > 0.");
        }
    }

    private void requireSeedValid(Integer seed, String field) {
        requireNonNull(seed, field);
        if (seed < 0 || seed > 100000) {
            throw new IllegalArgumentException(field + " must be between 0 and 100000.");
        }
    }

    private void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null.");
        }
    }

    private void requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank.");
        }
    }

    private void validateNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0.");
        }
    }

    private void validatePositive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be > 0.");
        }
    }
}