"use client";
import { useListState, useCounter, useSetState, useMap } from "@mantine/hooks";
import { createContext, RunwayModes } from "react";

export const RunwayModes = {
	MIXED_MODE: "mixed_mode",
	TAKEOFF_ONLY: "takeoff",
	LANDING_ONLY: "landing",
};

export const RunwayClosureMode = {
	SNOW_CLEARANCE: "snow_clearance",
	RUNWAY_INSPECTION: "runway_inspection",
	EQUIPMENT_FAILURE: "equipment_failure",
};

export const EmergencyStatus = {
	NONE: "none",
	MECHANICAL_FAIL: "mech_fail",
	PASSENGER_HEALTH: "passenger_health",
};

export const EmergencyStatusWithoutNone = {
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

const isPositiveInteger = (s) => Number.isInteger(s) && s > 0;
const isNaturalNumber = (s) => Number.isInteger(s) && s >= 0;
export const isValueInEnum = (e, v) => Object.values(e).includes(v);
export const objectHasProperties = (o, ...props) =>
	typeof o == "object" && props.every((prop) => o.hasOwnProperty(prop));
const generateAircraftSeed = () => Math.floor(Math.random() * 100000);

const DEFAULT_MAX_WAIT_MINUTES_BEFORE_TAKEOFF = 10;
const DEFAULT_FUEL_MINUTES_THRESHOLD_BEFORE_DIVERTED = 10;
const DEFAULT_MINUTES_TAKEN_FOR_TAKEOFF = 5;
const DEFAULT_MINUTES_TAKEN_FOR_LANDING = 5;

// References used:
// https://react.dev/reference/react/useReducer#adding-a-reducer-to-a-component
// https://react.dev/learn/reusing-logic-with-custom-hooks
// https://mantine.dev/hooks

export const useRunways = () => {
	const [
		values,
		{ append: valuesAppend, filter: valuesFilter, setState: valuesSet },
	] = useListState([]);
	const [counter, { increment: incrementCounter, set: setCounter }] =
		useCounter(0);

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

	const resetRunways = () => {
		return valuesSet([]);
	};

	const importRunways = (runways) => {
		if (!(runways instanceof Array)) throw Error("Invalid runways data");
		let maxCounter = 0;
		for (const runway of runways) {
			if (
				!objectHasProperties(runway, "id", "mode") ||
				!isValueInEnum(RunwayModes, runway.mode) ||
				!isNaturalNumber(runway.id)
			)
				throw Error("Invalid runways data");
			maxCounter = Math.max(maxCounter, runway.id);
		}

		valuesSet(runways);
		setCounter(maxCounter + 1);
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
		/**
		 * Removes all of the runways from the hook
		 */
		resetRunways,

		/**
		 * Used to import runways from a configuration file. Should not be used for initial configuration. Overrides the existing runways
		 * @param {Array} runways An array of formatted runways (formatted to the internal structure of a runway)
		 */
		importRunways,
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
			throw Error(
				"The maximum delay must be a whole number which is greater than or equal to zero",
			);
		}
		setConfig({ maxDelayBeforeCancelled: val });
	};

	const setFuelThresholdBeforeRedirected = (val) => {
		// This also checks whether val is of number type
		if (!isNaturalNumber(val)) {
			throw Error(
				"The minimum fuel minute redirection threshold must be a whole number which is greater than or equal to zero",
			);
		}
		setConfig({ fuelThresholdBeforeRedirected: val });
	};

	const setTimeTakenForTakeoff = (val) => {
		// This also checks whether val is of number type
		if (!isPositiveInteger(val)) {
			throw Error(
				"The number of minutes taken for a plane to take off must be a whole number which is greater than zero",
			);
		}
		setConfig({ timeTakenForTakeoff: val });
	};

	const setTimeTakenForLanding = (val) => {
		// This also checks whether val is of number type
		if (!isPositiveInteger(val)) {
			throw Error(
				"The number of minutes taken for a plane to land must be a whole number which is greater than zero",
			);
		}

		setConfig({ timeTakenForLanding: val });
	};

	const resetAdvancedConfig = () => {
		return setConfig({
			maxDelayBeforeCancelled: DEFAULT_MAX_WAIT_MINUTES_BEFORE_TAKEOFF,
			fuelThresholdBeforeRedirected:
				DEFAULT_FUEL_MINUTES_THRESHOLD_BEFORE_DIVERTED,
			timeTakenForTakeoff: DEFAULT_MINUTES_TAKEN_FOR_TAKEOFF,
			timeTakenForLanding: DEFAULT_MINUTES_TAKEN_FOR_LANDING,
		});
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
		/**
		 * Resets the advanced config to the default state
		 */
		resetAdvancedConfig,
	};
};

export const useFlightSchedule = () => {
	const flights = useMap();
	const [
		flightNumber,
		{ increment: incrementFlightNumber, set: setFlightNumber },
	] = useCounter(1);
	const addDepartureFlight = (
		operator,
		expected_departure_time,
		repeating,
		seed = generateAircraftSeed(),
	) => {
		if (typeof operator != "string" || operator.length < 2)
			throw Error("The operator must be at least two characters long");
		if (!isNaturalNumber(expected_departure_time))
			throw Error(
				"The expected departure time must be a whole number which is greater than or equal to zero",
			);

		if (typeof seed != "number") seed = generateAircraftSeed();

		if (
			repeating != undefined &&
			!objectHasProperties(repeating, "end", "period")
		)
			throw Error("Invalid repeating data");

		const flight = {
			callsign: operator.toUpperCase() + "-" + flightNumber.toString(),
			expected_departure_time,
			seed,
			repeating,
			id: flightNumber,
			type: FlightType.DEPARTURE,
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
		repeating,
		seed = generateAircraftSeed(),
	) => {
		if (typeof operator != "string" || operator.length < 2)
			throw Error("The operator must be at least two characters long");
		if (
			!isValueInEnum(EmergencyStatus, emergency_status) ||
			!isNaturalNumber(remaining_fuel_mins) ||
			!isNaturalNumber(expected_arrival_time)
		)
			throw Error("Invalid input parameters");

		if (typeof seed != "number") seed = generateAircraftSeed();

		if (
			repeating != undefined &&
			!objectHasProperties(repeating, "end", "period")
		)
			throw Error("Invalid repeating data");

		const flight = {
			callsign: operator.toUpperCase() + "-" + flightNumber.toString(),
			expected_arrival_time,
			emergency_status,
			remaining_fuel_mins,
			seed,
			id: flightNumber,
			type: FlightType.ARRIVAL,
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
		return found;
	};

	const resetFlightSchedule = () => {
		flights.clear();
		setFlightNumber(1);
	};

	const importFlightSchedule = (newFlights) => {
		resetFlightSchedule();
		if (!(newFlights instanceof Array))
			throw Error("Flight schedule is not an array");
		let maxCounter = 1;

		try {
			for (const [index, flight] of newFlights.entries()) {
				if (
					!isValueInEnum(FlightType, flight.type) ||
					!isNaturalNumber(flight.id) ||
					!isNaturalNumber(flight.seed)
				)
					throw Error("Flight data is malformed for flight at index: " + index);

				if (
					flight.repeating != undefined &&
					!objectHasProperties(flight.repeating, "end", "period")
				)
					throw Error(
						"Flight repeating data is invalid for flight at index: " + index,
					);

				if (typeof flight.callsign != "string" || flights.has(flight.callsign))
					throw Error(
						"Flight callsign is invalid for flight at index: " + index,
					);

				switch (flight.type) {
					case FlightType.ARRIVAL: {
						if (
							!isNaturalNumber(flight.expected_arrival_time) ||
							!isValueInEnum(EmergencyStatus, flight.emergency_status) ||
							!isNaturalNumber(flight.remaining_fuel_mins)
						)
							throw Error(
								"Invalid arrival flight data for flight at index: " + index,
							);
						break;
					}
					case FlightType.DEPARTURE: {
						if (!isNaturalNumber(flight.expected_departure_time))
							throw Error(
								"Invalid departure flight data for flight at index: " + index,
							);
						break;
					}
				}
				maxCounter = Math.max(maxCounter, flight.id);
				flights.set(flight.callsign, flight);
			}
		} catch (e) {
			resetFlightSchedule();
			throw e;
		}

		setFlightNumber(maxCounter + 1);
	};

	return {
		/**
		 * A map from callsign (can be used as a key) to the schedule data
		 * @type {Map<String,{type: FlightType.ARRIVAL, callsign: String, expected_arrival_time:Number, observed_arrival_time: Number, emergency_status: EmergencyStatus, remaining_fuel_mins: Number, repeating?:{ start: Number, end: Number, period: Number}} | { type: FlightType.DEPARTURE, callsign: String, expected_departure_time:Number, observed_departure_time:Number, repeating?:{ start: Number, end: Number, period: Number}}>}
		 * @see https://mantine.dev/hooks/use-map/ I would recommend using these docs to see how to display the flight schedule. Use the callsign as a key in lists.
		 */
		flights,

		/**
		 * Add a new departure flight to the schedule
		 * @param {String} operator The aircraft's operator e.g. EASYJET
		 * @param {Number} expected_departure_time The expected departure time (mins from start of simulation) when this plane should depart
		 * @param {{ end: Number, period: Number } | undefined} repeating [OPTIONAL] If the flight is repeating, provide the end of the repetition interval as well as the period (how frequently) this flight should repeat. The start of the interval is the expected arrival time of the initial flight.
		 * @param {Number} seed A seed used to generate samples in this on the backend. Ensures imported configurations will be comparable with their other uses in other simulations.
		 */
		addDepartureFlight,

		/**
		 * Adds a new arrival flight to the schedule
		 * @param {String} operator The aircraft's operator e.g. EASYJET
		 * @param {valueof EmergencyStatus} emergency_status The emergency status of this flight
		 * @param {Number} remaining_fuel_mins The number of minutes remaining before this flight has no fuel
		 * @param {Number} expected_arrival_time The expected arrival time (mins from start of simulation) when this plane should arrive
		 * @param {{ end: Number, period: Number} | undefined} repeating [OPTIONAL] If the flight is repeating, provide the start and end of the repetition interval as well as the period (how frequently) this flight should repeat. The start of the interval is the expected arrival time of the initial flight.
		 * @param {Number} seed A seed used to generate samples in this on the backend. Ensures imported configurations will be comparable with their other uses in other simulations.
		 */
		addArrivalFlight,
		/**
		 * @param {String} callsign The callsign of the flight to remove
		 */
		removeFlight,

		/**
		 * Resets the flight schedule to the default state
		 */
		resetFlightSchedule,

		/**
		 * Used to import the flight schedule from a configuration file. Should not be used for initial configuration. Overrides the existing flight schedule
		 * @param {Array} newFlights An array of formatted flights (formatted to the internal structure of a flight schedule)
		 */
		importFlightSchedule,
	};
};

export const useHazardSchedule = () => {
	const hazards = useMap();
	const [
		hazardCounter,
		{ increment: incrementHazardCounter, set: setHazardCounter },
	] = useCounter(1);

	const addRunwayClosureHazard = (
		startTimeMinutes,
		durationMinutes,
		affectedRunway,
		closureMode,
		repeating,
	) => {
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

		if (
			repeating != undefined &&
			!objectHasProperties(repeating, "end", "period")
		)
			throw Error("Invalid repeating data");

		const hazard = {
			id: hazardCounter,
			type: HazardType.RUNWAY_CLOSURE,
			start_time_mins: startTimeMinutes,
			duration_mins: durationMinutes,
			affected_runway: affectedRunway,
			closure_mode: closureMode,
			repeating,
		};

		const res = hazards.set(hazardCounter, hazard);
		incrementHazardCounter();

		return res;
	};

	const addEmergencyEventHazard = (arrivalCallsign, emergencyType, time) => {
		if (
			typeof arrivalCallsign !== "string" ||
			arrivalCallsign.trim().length === 0
		)
			throw new Error("Emergency event must target a valid arrival callsign");
		if (typeof time !== "number") throw new Error("Emergency time is invalid");
		if (!isValueInEnum(EmergencyStatusWithoutNone, emergencyType)) {
			throw new Error("Invalid emergency status");
		}

		const hazard = {
			id: hazardCounter,
			type: HazardType.EMERGENCY_EVENT,
			target_arrival_callsign: arrivalCallsign,
			time,
		};

		const res = hazards.set(hazardCounter, hazard);
		incrementHazardCounter();
		return res;
	};

	const removeHazard = (hazardId) => {
		const found = hazards.delete(hazardId);
		if (!found)
			throw Error("Unable to remove hazard, was not found within schedule");
		return found;
	};

	const removeHazardsForAircraft = (callsign) => {
		for (const [hazardId, hazardData] of hazards.entries()) {
			if (
				hazardData.type == HazardType.EMERGENCY_EVENT &&
				hazardData.target_arrival_callsign == callsign
			)
				hazards.delete(hazardId);
		}
		return;
	};

	const resetHazardSchedule = () => {
		return hazards.clear();
	};

	const importHazardSchedule = (newHazards, flightSchedule, runways) => {
		resetHazardSchedule();
		if (!(newHazards instanceof Array))
			throw Error("Hazard schedule is not an array");
		let maxCounter = 1;

		try {
			for (const [index, hazard] of newHazards.entries()) {
				if (
					!objectHasProperties(hazard, "type", "id") ||
					!isValueInEnum(HazardType, hazard.type) ||
					!isNaturalNumber(hazard.id)
				)
					throw Error("Hazard data is malformed for hazard at index:" + index);

				if (callsigns.has(hazard.id))
					throw Error("Hazard id is repeated in hazard at index:" + index);

				switch (hazard.type) {
					case HazardType.EMERGENCY_EVENT: {
						if (!flightSchedule.has(hazard.target_arrival_callsign))
							throw Error(
								"Hazard at index " + index + " applied to nonexistent flight: ",
							);

						if (!isNaturalNumber(hazard.time))
							throw Error(
								"Invalid or missing hazard time for hazard at index:" + index,
							);
						break;
					}
					case HazardType.RUNWAY_CLOSURE: {
						if (!runways.find((r) => r.id == hazard.affected_runway))
							throw Error(
								"Runway closure applied to nonexistent runway id for hazard at index:" +
									index,
							);

						if (
							!isNaturalNumber(hazard.start_time_mins) ||
							!isNaturalNumber(hazard.duration_mins) ||
							!isValueInEnum(RunwayClosureMode, hazard.closure_mode)
						)
							throw Error(
								"Runway closure event is missing or has invalid required properties for hazard at index:" +
									index,
							);

						if (
							hazard.repeating != undefined &&
							!objectHasProperties(hazard.repeating, "end", "period")
						)
							throw Error(
								"Hazard repeating data is invalid for hazard at index:" + index,
							);
						break;
					}
				}
				maxCounter = Math.max(maxCounter, hazard.id);
				hazards.set(hazard.id, hazard);
			}
		} catch (e) {
			resetHazardSchedule();
			throw e;
		}

		setHazardCounter(maxCounter + 1);
	};
	return {
		/**
		 * A map from hazard id (can be used as a key) to the hazard schedule data
		 * @type {Map<Number,{id: Number, type: HazardType.RUNWAY_CLOSURE, start_time_mins: Number, duration_mins: Number, affected_runway: Number, closure_mode: closureMode, repeating?: {}}>}
		 * @see https://mantine.dev/hooks/use-map/ I would recommend using these docs to see how to display the hazard schedule. Use the hazard id as a key in lists.
		 */
		hazards,
		/**
		 * Adds a runway closure hazard to the schedule
		 * @param {Number} startTimeMinutes The time when the closure event should start
		 * @param {Number} durationMinutes The duration of the closure event
		 * @param {Number} affectedRunway The runway affected by the closure
		 * @param {Number} closureMode The type of closure to apply to the runway
		 * @param {{ end: Number, period: Number } | undefined} repeating [OPTIONAL] If the hazard is repeating, provide the end of the repetition interval as well as the period (how frequently) this hazard event should repeat. The duration and start will be inferred from the inital event.
		 */
		addRunwayClosureHazard,
		/**
		 * Adds an emergency event hazard to the schedule. Note that this sort of hazard is not schedulable for repeating flights and sanitisation should be included to ensure this
		 * @param {String} arrivalCallsign The callsign of the aircraft to add the emergency event to (sanitisation is not included)
		 * @param {EmergencyStatusWithoutNone} emergencyType The type of emergency which the aircraft will experience
		 * @param {Number} time The time (in minutes after the simulation starts) after which the emergency event will be applied if the plane hasn't already landed (sanitisation is not included)
		 */
		addEmergencyEventHazard,
		/**
		 * Remove a hazard from the schedule
		 * @param {Number} hazardId The id of the hazard to remove from the schedule
		 */
		removeHazard,

		/**
		 * @param {String} callsign The callsign of the aircraft to remove from the hazard schedule
		 */
		removeHazardsForAircraft,

		/**
		 * Resets the hazard schedule to the default state
		 */
		resetHazardSchedule,

		/**
		 * Used to import the hazard schedule from a configuration file. Should not be used for initial configuration. Overrides the existing hazard schedule
		 * @param {Array} newHazards An array of formatted hazards (formatted to the internal structure of a hazard schedule)
		 */
		importHazardSchedule,
	};
};

export const ConfigContext = createContext();

/**
 * A context provider for the client-sided configuration hooks
 * @returns A react component providing context through the ConfigContext context
 */
export const ConfigProvider = ({ children }) => {
	const {
		hazards,
		addRunwayClosureHazard,
		addEmergencyEventHazard,
		removeHazard,
		removeHazardsForAircraft,
		resetHazardSchedule,
		importHazardSchedule,
	} = useHazardSchedule();
	const {
		flights,
		addDepartureFlight,
		addArrivalFlight,
		removeFlight,
		resetFlightSchedule,
		importFlightSchedule,
	} = useFlightSchedule();
	const {
		advancedConfig,
		setMaxDelayBeforeCancelled,
		setFuelThresholdBeforeRedirected,
		setTimeTakenForLanding,
		setTimeTakenForTakeoff,
		resetAdvancedConfig,
	} = useAdvancedConfig();
	const { runways, addRunway, removeRunway, resetRunways, importRunways } =
		useRunways();

	// References used: https://www.w3schools.com/react/react_usecontext.asp
	return (
		<ConfigContext.Provider
			value={{
				hazards,
				addRunwayClosureHazard,
				addEmergencyEventHazard,
				removeHazard,
				removeHazardsForAircraft,
				resetHazardSchedule,
				importHazardSchedule,
				flights,
				addDepartureFlight,
				addArrivalFlight,
				removeFlight,
				resetFlightSchedule,
				importFlightSchedule,
				advancedConfig,
				setMaxDelayBeforeCancelled,
				setFuelThresholdBeforeRedirected,
				setTimeTakenForLanding,
				setTimeTakenForTakeoff,
				resetAdvancedConfig,
				runways,
				addRunway,
				removeRunway,
				resetRunways,
				importRunways,
			}}
		>
			{children}
		</ConfigContext.Provider>
	);
};
