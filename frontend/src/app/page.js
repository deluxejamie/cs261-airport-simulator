"use client";
import { useCounter, useListState } from "@mantine/hooks";
import React, { useReducer } from "react";
const RunwayModes = {
	MIXED_MODE: "mixed_mode",
	TAKEOFF_ONLY: "takeoff",
	LANDING_ONLY: "landing",
};

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

	return { runways: values, addRunway, removeRunway };
};
export default function Home() {
	const {
		/**
		 * An array of Runways. The id prop should be used as a key in lists.
		 * Each runway is a POJO with the following properties:
		 * {
		 *   id: runwayCounter,
		 *   mode: valueof RunwayModes
		 * }
		 */
		runways,
		/**
		 * Adds a runway to the stored list
		 * Example usage:
		 * ```
		 * addRunway(RunwayModes.MIXED_MODE)
		 * ```
		 * @param {valueof RunwayModes} mode The new runway's mode
		 */
		addRunway,
		/**
		 * Removes a runway from the stored list using its id
		 * Example usage:
		 * ```
		 * removeRunway(12)
		 * ```
		 * @param {Number} id The id of the runway to remove
		 */
		removeRunway,
	} = useRunways();

	return <></>;
}
