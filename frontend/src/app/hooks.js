"use client";

import { useCallback, useState } from "react";

export const EmergencyStatus = {
  NONE: "none",
  FUEL: "fuel",
  MECHANICAL_FAIL: "mech_fail",
  PASSENGER_HEALTH: "passenger_health",
};

export const FlightType = {
  ARRIVAL: "arrival",
  DEPARTURE: "departure",
};

export const HazardType = {
  RUNWAY_CLOSURE: "runway_closure",
  EMERGENCY_EVENT: "emergency_event",
};

export const RunwayClosureMode = {
  SNOW_CLEARANCE: "snow_clearance",
  RUNWAY_INSPECTION: "runway_inspection",
  EQUIPMENT_FAILURE: "equipment_failure",
};

const OBSERVED_TIME_STD_DEV_MINUTES = 5;

const isNaturalNumber = (value) => Number.isInteger(value) && value >= 0;
const isPositiveInteger = (value) => Number.isInteger(value) && value > 0;
const isValueInEnum = (enumObj, value) => Object.values(enumObj).includes(value);

const normalizeOperatorForCallsign = (operator) => {
  if (typeof operator !== "string") {
    throw new Error("Aircraft operator must be a valid string");
  }

  const normalized = operator
    .trim()
    .toUpperCase()
    .replace(/[^A-Z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");

  if (normalized.length < 2) {
    throw new Error("Aircraft operator must contain at least 2 characters");
  }

  return normalized;
};

const normalSample = (mean, stdDev) => {
  let u = 0;
  let v = 0;

  while (u === 0) u = Math.random();
  while (v === 0) v = Math.random();

  const gaussian = Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
  return Math.round(mean + gaussian * stdDev);
};

const generateObservedTime = (expectedTime) =>
  Math.max(0, normalSample(expectedTime, OBSERVED_TIME_STD_DEV_MINUTES));

export const useFlightSchedule = () => {
  const [flights, setFlights] = useState([]);
  const [callsignCounter, setCallsignCounter] = useState(1);

  const generateCallsign = useCallback(
    (operator) => {
      const operatorPrefix = normalizeOperatorForCallsign(operator);
      return `${operatorPrefix}-${callsignCounter}`;
    },
    [callsignCounter],
  );

  const addDepartureFlight = useCallback(
    (operator, expectedDepartureTime) => {
      if (!isNaturalNumber(expectedDepartureTime)) {
        throw new Error("Expected departure time must be a natural number");
      }

      const normalizedOperator = normalizeOperatorForCallsign(operator);
      const departure = {
        type: FlightType.DEPARTURE,
        operator: normalizedOperator,
        callsign: generateCallsign(operator),
        expected_departure_time: expectedDepartureTime,
        observed_departure_time: generateObservedTime(expectedDepartureTime),
      };

      setFlights((prev) => [...prev, departure]);
      setCallsignCounter((prev) => prev + 1);
      return departure;
    },
    [generateCallsign],
  );

  const addArrivalFlight = useCallback(
    (operator, expectedArrivalTime, remainingFuelMinutes, emergencyStatus) => {
      if (!isNaturalNumber(expectedArrivalTime)) {
        throw new Error("Expected arrival time must be a natural number");
      }
      if (!isPositiveInteger(remainingFuelMinutes)) {
        throw new Error("Fuel at arrival must be a positive integer");
      }
      if (!isValueInEnum(EmergencyStatus, emergencyStatus)) {
        throw new Error("Emergency status is invalid");
      }

      const normalizedOperator = normalizeOperatorForCallsign(operator);
      const arrival = {
        type: FlightType.ARRIVAL,
        operator: normalizedOperator,
        callsign: generateCallsign(operator),
        remaining_fuel_mins: remainingFuelMinutes,
        emergency_status: emergencyStatus,
        expected_arrival_time: expectedArrivalTime,
        observed_arrival_time: generateObservedTime(expectedArrivalTime),
      };

      setFlights((prev) => [...prev, arrival]);
      setCallsignCounter((prev) => prev + 1);
      return arrival;
    },
    [generateCallsign],
  );

  const removeFlight = useCallback((callsign) => {
    setFlights((prev) => {
      const nextFlights = prev.filter((flight) => flight.callsign !== callsign);
      if (nextFlights.length === prev.length) {
        throw new Error("Unable to remove flight, callsign was not found");
      }
      return nextFlights;
    });
  }, []);

  return {
    flights,
    addDepartureFlight,
    addArrivalFlight,
    removeFlight,
  };
};

export const useHazardSchedule = () => {
  const [hazards, setHazards] = useState([]);
  const [hazardIdCounter, setHazardIdCounter] = useState(1);

  const addRunwayClosureHazard = useCallback(
    (startTimeMinutes, durationMinutes, affectedRunway, closureMode) => {
      if (!isNaturalNumber(startTimeMinutes)) {
        throw new Error("Runway closure start time must be a natural number");
      }
      if (!isPositiveInteger(durationMinutes)) {
        throw new Error("Runway closure duration must be a positive integer");
      }
      if (!isPositiveInteger(affectedRunway)) {
        throw new Error("Affected runway must be a positive integer");
      }
      if (!isValueInEnum(RunwayClosureMode, closureMode)) {
        throw new Error("Runway closure mode is invalid");
      }

      const hazard = {
        id: hazardIdCounter,
        type: HazardType.RUNWAY_CLOSURE,
        start_time_mins: startTimeMinutes,
        duration_mins: durationMinutes,
        affected_runway: affectedRunway,
        closure_mode: closureMode,
      };

      setHazards((prev) => [...prev, hazard]);
      setHazardIdCounter((prev) => prev + 1);
      return hazard;
    },
    [hazardIdCounter],
  );

  const addEmergencyEventHazard = useCallback(
    (arrivalCallsign) => {
      if (typeof arrivalCallsign !== "string" || arrivalCallsign.trim().length === 0) {
        throw new Error("Emergency event must target a valid arrival callsign");
      }

      const hazard = {
        id: hazardIdCounter,
        type: HazardType.EMERGENCY_EVENT,
        target_arrival_callsign: arrivalCallsign,
      };

      setHazards((prev) => [...prev, hazard]);
      setHazardIdCounter((prev) => prev + 1);
      return hazard;
    },
    [hazardIdCounter],
  );

  const removeHazard = useCallback((hazardId) => {
    setHazards((prev) => {
      const nextHazards = prev.filter((hazard) => hazard.id !== hazardId);
      if (nextHazards.length === prev.length) {
        throw new Error("Unable to remove hazard, id was not found");
      }
      return nextHazards;
    });
  }, []);

  return {
    hazards,
    addRunwayClosureHazard,
    addEmergencyEventHazard,
    removeHazard,
  };
};