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
	getSimulationConfiguration,
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
const SimulationEventLogComponent = ({ uuid: _uuid }) => {
	// display the simulation event log (SCRUM-37)

	return <></>;
};

/**
 * @param {{ uuid: String}} param0 The uuid for the simulation
 * @returns A react component
 */
const SimulationStatsComponent = ({ uuid }) => {
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	const [result, setResult] = useState(null);
	const [exporting, setExporting] = useState(false);

	useEffect(() => {
		let isMounted = true;
		(async () => {
			setLoading(true);
			setError(null);
			try {
				const fetchedResult = await getSimulationResult(uuid);
				if (!isMounted) return;
				setResult(fetchedResult);
			} catch (e) {
				if (!isMounted) return;
				setError("Failed to load simulation statistics.");
			} finally {
				if (isMounted) setLoading(false);
			}
		})();
		return () => {
			isMounted = false;
		};
	}, [uuid]);
};
