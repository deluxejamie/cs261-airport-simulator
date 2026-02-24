"use client";
import { useListState, useCounter, useSetState } from "@mantine/hooks";

export const RunwayModes = {
	MIXED_MODE: "mixed_mode",
	TAKEOFF_ONLY: "takeoff",
	LANDING_ONLY: "landing",
};

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
		if (!Object.values(RunwayModes).includes(mode))
			throw Error("Invalid runway mode");

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
		if (!Number.isInteger(val) || val < 0) {
			throw Error("Improper argument provided");
		}
		setConfig({ maxDelayBeforeCancelled: val });
	};

	const setFuelThresholdBeforeRedirected = (val) => {
		// This also checks whether val is of number type
		if (!Number.isInteger(val) || val < 0) {
			throw Error("Improper argument provided");
		}
		setConfig({ setFuelThresholdBeforeRedirected: val });
	};

	const setTimeTakenForTakeoff = (val) => {
		// This also checks whether val is of number type
		if (!Number.isInteger(val) || val <= 0) {
			throw Error("Improper argument provided");
		}
		setConfig({ timeTakenForTakeoff: val });
	};

	const setTimeTakenForLanding = (val) => {
		// This also checks whether val is of number type
		if (!Number.isInteger(val) || val <= 0) {
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
