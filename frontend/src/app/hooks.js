"use client";
import { useListState, useCounter, useSetState, useMap } from "@mantine/hooks";

export const RunwayModes = {
	MIXED_MODE: "mixed_mode",
	TAKEOFF_ONLY: "takeoff",
	LANDING_ONLY: "landing",
};

export const EmergencyStatus = {
	NONE: "none",
	MECHANICAL_FAIL: "mech_fail",
	PASSENGER_HEALTH: "passenger_health",
};

export const FlightType = {
	ARRIVAL: "arrival",
	DEPARTURE: "departure",
};

const isPositiveInteger = (s) => Number.isInteger(s) && s > 0;
const isNaturalNumber = (s) => Number.isInteger(s) && s >= 0;
const isValueInEnum = (e, v) => Object.values(e).includes(v);

const DEFAULT_MAX_WAIT_MINUTES_BEFORE_TAKEOFF = 10;
const DEFAULT_FUEL_MINUTES_THRESHOLD_BEFORE_DIVERTED = 10;
const DEFAULT_MINUTES_TAKEN_FOR_TAKEOFF = 5;
const DEFAULT_MINUTES_TAKEN_FOR_LANDING = 5;

// References used:
// https://react.dev/reference/react/useReducer#adding-a-reducer-to-a-component
// https://react.dev/learn/reusing-logic-with-custom-hooks
// https://mantine.dev/hooks

export const useRunways = () => {
	const [values, { append: valuesAppend, filter: valuesFilter }] = useListState(
		[],
	);
	const [counter, { increment: incrementCounter }] = useCounter(0);

	const addRunway = (mode) => {
		if (!isValueInEnum(RunwayModes, mode)) throw Error("Invalid runway mode");

		if (values.length == 10)
			throw Error("There are already 10 runways being stored (max reached)");
		const res = valuesAppend({ id: counter, mode });
		incrementCounter();
		return res;
	};

	const removeRunway = (id) => {
		const sizebefore = values.length;
		const res = valuesFilter((r) => r.id !== id);
		if (res.length == sizebefore)
			throw Error("Attempted to remove a runway which does not exist");
		return res;
	};

	return {
		/**
		 * An array of Runways. The id prop should be used as a key in lists.
		 * Each runway is a POJO with the following properties:
		 * {
		 *   id: runwayCounter,
		 *   mode: valueof RunwayModes
		 * }
		 */
		runways: values,
		/**
		 * Adds a runway to the stored list. Example usage: ```addRunway(RunwayModes.MIXED_MODE)```
		 * @param {valueof RunwayModes} mode The new runway's mode
		 */
		addRunway,
		/**
		 * Removes a runway from the stored list using its id. Example usage: ```removeRunway(12)```
		 * @param {Number} id The id of the runway to remove
		 */
		removeRunway,
	};
};

export const useAdvancedConfig = () => {
	const [advancedConfig, setConfig] = useSetState({
		maxDelayBeforeCancelled: DEFAULT_MAX_WAIT_MINUTES_BEFORE_TAKEOFF,
		fuelThresholdBeforeRedirected:
			DEFAULT_FUEL_MINUTES_THRESHOLD_BEFORE_DIVERTED,
		timeTakenForTakeoff: DEFAULT_MINUTES_TAKEN_FOR_TAKEOFF,
		timeTakenForLanding: DEFAULT_MINUTES_TAKEN_FOR_LANDING,
	});

	const setMaxDelayBeforeCancelled = (val) => {
		// This also checks whether val is of number type
		if (!isNaturalNumber(val)) {
			throw Error("Improper argument provided");
		}
		setConfig({ maxDelayBeforeCancelled: val });
	};

	const setFuelThresholdBeforeRedirected = (val) => {
		// This also checks whether val is of number type
		if (!isNaturalNumber(val)) {
			throw Error("Improper argument provided");
		}
		setConfig({ setFuelThresholdBeforeRedirected: val });
	};

	const setTimeTakenForTakeoff = (val) => {
		// This also checks whether val is of number type
		if (!isPositiveInteger(val)) {
			throw Error("Improper argument provided");
		}
		setConfig({ timeTakenForTakeoff: val });
	};

	const setTimeTakenForLanding = (val) => {
		// This also checks whether val is of number type
		if (!isPositiveInteger(val)) {
			throw Error("Improper argument provided");
		}
		setConfig({ timeTakenForLanding: val });
	};
	return {
		/**
		 * @type {{maxDelayBeforeCancelled: Number, fuelThresholdBeforeRedirected: Number, timeTakenForTakeoff: Number, timeTakenForLanding: Number}}
		 */
		advancedConfig,
		/**
		 * @param {Number} val The maximum delay for a flight before it should be cancelled (in minutes)
		 */
		setMaxDelayBeforeCancelled,
		/**
		 * @param {Number} val The threshold amount of fuel remaining in minutes before a flight must be redirected
		 */
		setFuelThresholdBeforeRedirected,
		/**
		 * @param {Number} val The time taken for a flight to take off in minutes
		 */
		setTimeTakenForTakeoff,
		/**
		 * @param {Number} val The time taken for a flight to land in minutes
		 */
		setTimeTakenForLanding,
	};
};

export const useFlightSchedule = () => {
	const flights = useMap();
	const [flightNumber, { increment: incrementFlightNumber }] = useCounter(0);
	const addDepartureFlight = (operator, expected_departure_time, repeating) => {
		if (
			typeof operator != "string" ||
			!isNaturalNumber(expected_departure_time)
		)
			throw Error("Invalid input parameters");
		if (!isNaturalNumber(observed_departure_time)) {
			// todo: generate observed departure time using normal distribution
			observed_departure_time = 0;
		}

		if (
			repeating != undefined &&
			(!repeating.hasOwnProperty("start") ||
				!repeating.hasOwnProperty("end") ||
				!repeating.hasOwnProperty("period"))
		)
			throw Error("Invalid repeating data");

		const flight = {
			callsign: operator.toUpperCase() + "-" + flightNumber.toString(),
			expected_departure_time,
			observed_departure_time,
			repeating,
		};
		const res = flights.set(flight.callsign, flight);
		incrementFlightNumber();
		return res;
	};

	const addArrivalFlight = (
		operator,
		emergency_status,
		remaining_fuel_mins,
		expected_arrival_time,
		observed_arrival_time,
		repeating,
	) => {
		if (
			typeof operator != "string" ||
			!isValueInEnum(EmergencyStatus, emergency_status) ||
			!isNaturalNumber(remaining_fuel_mins) ||
			!isNaturalNumber(expected_arrival_time)
		)
			throw Error("Invalid input parameters");

		if (!isNaturalNumber(observed_arrival_time)) {
			// todo: generate observed arrival time
			observed_arrival_time = 0;
		}

		if (
			repeating != undefined &&
			(!repeating.hasOwnProperty("start") ||
				!repeating.hasOwnProperty("end") ||
				!repeating.hasOwnProperty("period"))
		)
			throw Error("Invalid repeating data");

		const flight = {
			callsign: operator.toUpperCase() + "-" + flightNumber.toString(),
			expected_arrival_time,
			observed_arrival_time,
			emergency_status,
			remaining_fuel_mins,
			repeating,
		};
		const res = flights.set(flight.callsign, flight);
		incrementFlightNumber();
		return res;
	};

	const removeFlight = (callsign) => {
		const found = flights.delete(callsign);
		if (!found)
			throw Error("Unable to remove flight, was not found within schedule");
	};

	return {
		/**
		 * A map from callsign (can be used as a key) to the schedule data
		 * @type {Map<String,{type: FlightType.ARRIVAL, callsign: String, expected_arrival_time:Number, observed_arrival_time: Number, emergency_status: EmergencyStatus, remaining_fuel_mins: Number, repeating?:{ start: Number, end: Number, period: Number}} | { type: FlightType.DEPARTURE, callsign: String, expected_departure_time:Number, observed_departure_time:Number, repeating?:{ start: Number, end: Number, period: Number}}>}
		 * @see https://mantine.dev/hooks/use-map/ I would recommend using these docs to see how to display the flight schedule. Use the callsign as a key.
		 */
		flights,

		/**
		 * Add a new departure flight to the schedule
		 * @param {String} operator The aircraft's operator e.g. EASYJET
		 * @param {Number} expected_departure_time The expected departure time (mins from start of simulation) when this plane should depart
		 * @param {Number} observed_departure_time [OPTIONAL] The observed departure time (mins from the start of the simulation) when this plane should depart. If not provided, will be generated using normal dist from expected.
		 * @param {{ start: Number, end: Number, period: Number} | undefined} repeating [OPTIONAL] If the flight is repeating, provide the start and end of the repetition interval as well as the period (how frequently) this flight should repeat
		 */
		addDepartureFlight,

		/**
		 * Adds a new arrival flight to the schedule
		 * @param {String} operator The aircraft's operator e.g. EASYJET
		 * @param {valueof EmergencyStatus} emergency_status The emergency status of this flight
		 * @param {Number} remaining_fuel_mins The number of minutes remaining before this flight has no fuel
		 * @param {Number} expected_arrival_time The expected arrival time (mins from start of simulation) when this plane should arrive
		 * @param {Number} observed_arrival_time [OPTIONAL] The observed arrival time (mins from the start of the simulation) when this plane should arrive. If not provided, will be generated using normal dist from expected.
		 * @param {{ start: Number, end: Number, period: Number} | undefined} repeating [OPTIONAL] If the flight is repeating, provide the start and end of the repetition interval as well as the period (how frequently) this flight should repeat
		 */
		addArrivalFlight,
		/**
		 * @param {String} callsign The callsign of the flight to remove
		 */
		removeFlight,
	};
};

export const useHazardSchedule = () => {
	const hazards = useMap();
};
