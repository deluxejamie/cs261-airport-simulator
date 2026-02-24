"use client";
import { useCounter, useListState } from "@mantine/hooks";
import React, { useEffect, useReducer } from "react";
import { useRunways, RunwayModes } from "./hooks";
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
