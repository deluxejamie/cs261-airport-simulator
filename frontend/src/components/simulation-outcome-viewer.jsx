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

	const rows = useMemo(() => {
		if (!result) return [];
		return [
			["Max take-off queue", result.maxTakeOffQueue],
			["Average take-off wait (mins)", formatDecimal(result.avgTakeOffWait)],
			["Max holding queue", result.maxHoldQueue],
			["Average hold time (mins)", formatDecimal(result.avgHoldTime)],
			["Total cancellations", result.totalCancellations],
			["Total diversions", result.totalDiversions],
			["Average arrival delay (mins)", formatDecimal(result.avgArrivalDelay)],
			[
				"Average departure delay (mins)",
				formatDecimal(result.avgDepartureDelay),
			],
		];
	}, [result]);

	const downloadConfiguration = useCallback(async () => {
		setExporting(true);
		setError(null);
		try {
			const response = await getSimulationConfiguration(uuid);
			const configString =
				typeof response === "string"
					? response
					: response?.config ?? JSON.stringify(response, null, 2);

			const tempLink = document.createElement("a");
			tempLink.href = window.URL.createObjectURL(new Blob([configString]));
			tempLink.setAttribute("download", `simulation_${uuid}_config.json`);
			document.body.appendChild(tempLink);
			tempLink.click();
			document.body.removeChild(tempLink);
		} catch (e) {
			setError("Failed to download the simulation configuration.");
		} finally {
			setExporting(false);
		}
	}, [uuid]);
	return (
		<Card withBorder>
			<Stack>
				<Title order={2}>Simulation statistics</Title>
				{loading ? (
					<Loader size="sm" />
				) : error ? (
					<Alert color="red">{error}</Alert>
				) : (
					<>
						<Table striped withTableBorder>
							<Table.Tbody>
								{rows.map(([label, value]) => (
									<Table.Tr key={label}>
										<Table.Td>{label}</Table.Td>
										<Table.Td>{value}</Table.Td>
									</Table.Tr>
								))}
							</Table.Tbody>
						</Table>
						<Button
							fullWidth
							variant="light"
							leftSection={<IconDownload size={16} />}
							loading={exporting}
							onClick={downloadConfiguration}
						>
							Export configuration
						</Button>
					</>
				)}
			</Stack>
		</Card>
	);
};
