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

const isNaturalNumber = (value) => Number.isInteger(value) && value >= 0;
const isPositiveInteger = (value) => Number.isInteger(value) && value > 0;
const isValueInEnum = (enumObj, value) => Object.values(enumObj).includes(value);

const normalSample = (mean, stdDev) => {
  let u = 0;
  let v = 0;

  while (u === 0) u = Math.random();
  while (v === 0) v = Math.random();

  const z = Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
  return Math.round(mean + z * stdDev);
};

const generateObservedTime = (expectedTime, stdDevMinutes = 5) => {
  const sampled = normalSample(expectedTime, stdDevMinutes);
  return Math.max(0, sampled);
};

export const useFlightSchedule = () => {
  const [flights, setFlights] = useState([]);
  const [flightNumber, setFlightNumber] = useState(1);

  const nextCallsign = useCallback(
    (operator) => {
      if (typeof operator !== "string" || operator.trim().length < 2) {
        throw new Error("Operator must be a non-empty string");
      }
      return `${operator.trim().toUpperCase()}-${flightNumber}`;
    },
    [flightNumber],
  );

  const addDepartureFlight = useCallback(
    (operator, expectedDepartureTime, observedDepartureTime) => {
      if (!isNaturalNumber(expectedDepartureTime)) {
        throw new Error("Expected departure time must be a natural number");
      }

      const callsign = nextCallsign(operator);
      const observed = isNaturalNumber(observedDepartureTime)
        ? observedDepartureTime
        : generateObservedTime(expectedDepartureTime, 5);

      const flight = {
        type: FlightType.DEPARTURE,
        callsign,
        operator: operator.trim(),
        expected_departure_time: expectedDepartureTime,
        observed_departure_time: observed,
      };

      setFlights((prev) => [...prev, flight]);
      setFlightNumber((prev) => prev + 1);
      return flight;
    },
    [nextCallsign],
  );

  const addArrivalFlight = useCallback(
    (
      operator,
      emergencyStatus,
      remainingFuelMins,
      expectedArrivalTime,
      observedArrivalTime,
    ) => {
      if (!isValueInEnum(EmergencyStatus, emergencyStatus)) {
        throw new Error("Invalid emergency status");
      }
      if (!isPositiveInteger(remainingFuelMins)) {
        throw new Error("Remaining fuel minutes must be positive");
      }
      if (!isNaturalNumber(expectedArrivalTime)) {
        throw new Error("Expected arrival time must be a natural number");
      }

      const callsign = nextCallsign(operator);
      const observed = isNaturalNumber(observedArrivalTime)
        ? observedArrivalTime
        : generateObservedTime(expectedArrivalTime, 5);

      const flight = {
        type: FlightType.ARRIVAL,
        callsign,
        operator: operator.trim(),
        emergency_status: emergencyStatus,
        remaining_fuel_mins: remainingFuelMins,
        expected_arrival_time: expectedArrivalTime,
        observed_arrival_time: observed,
      };

      setFlights((prev) => [...prev, flight]);
      setFlightNumber((prev) => prev + 1);
      return flight;
    },
    [nextCallsign],
  );

  const removeFlight = useCallback((callsign) => {
    setFlights((prev) => {
      const next = prev.filter((f) => f.callsign !== callsign);
      if (next.length === prev.length) {
        throw new Error("Unable to remove flight, it was not found");
      }
      return next;
    });
  }, []);

  return {
    flights,
    addDepartureFlight,
    addArrivalFlight,
    removeFlight,
  };
};