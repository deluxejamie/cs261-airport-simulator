"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import {
	Alert,
	Badge,
	Button,
	Card,
	Group,
	Loader,
	Stack,
	Table,
	Text,
	Title,
} from "@mantine/core";
import {
	getSimulationEventLog,
	getSimulationResult,
	getSimulationStatus,
} from "@/lib/simulation-api";

export default function SimulationOutcomeFoundPage({ uuid }) {
	return (
		<>
			<SimulationEventLogComponent uuid={uuid} />
			<SimulationStatsComponent uuid={uuid} />
		</>
	);
}

/**
 * @param {{ uuid: String}} param0 The uuid for the simulation
 * @returns A react component
 */
const SimulationEventLogComponent = ({ uuid }) => {
	// display the simulation event log (SCRUM-37)

	return <></>;
};

/**
 * @param {{ uuid: String}} param0 The uuid for the simulation
 * @returns A react component
 */
const SimulationStatsComponent = ({ uuid }) => {
	// display the stats for the simulation (SCRUM-36)
	// todo for yasvi
	return <></>;
};
