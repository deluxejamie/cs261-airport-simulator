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

export const POLL_EXPONENTIAL_RATE = 1.5;
export const MAX_FAILED_ATTEMPTS = 2;

export default function SimulationOutcomeFoundPage({ uuid }) {
	const [result, setResult] = useState(null);
	const [eventLog, setEventLog] = useState([]);
	const [eventError, setEventError] = useState("");
	const [logOffset, setLogOffset] = useState(0);
	const [loadingEvents, setLoadingEvents] = useState(false);

	useEffect(() => {
		if (!isCompleted) return;

		const loadResult = async () => {
			try {
				const data = await getSimulationResult(uuid);
				setResult(data);
			} catch (error) {
				setPollError(error.message);
			}
		};

		loadResult();
	}, [isCompleted, uuid]);

	const loadMoreEvents = useCallback(async () => {
		if (loadingEvents || !isCompleted) return;
		setLoadingEvents(true);
		try {
			const data = await getSimulationEventLog(uuid, logOffset, 30);
			const events = data?.events || data || [];
			setEventLog((prev) => [...prev, ...events]);
			setLogOffset((prev) => prev + events.length);
			setEventError("");
		} catch {
			try {
				await getSimulationEventLog(uuid, logOffset, 30);
			} catch {
				setEventError("Failed to load remaining events.");
			}
		} finally {
			setLoadingEvents(false);
		}
	}, [isCompleted, loadingEvents, logOffset, uuid]);

	const rows = useMemo(
		() =>
			eventLog.map((entry, index) => (
				<Table.Tr key={`${entry.timestamp || index}-${entry.type || "event"}`}>
					<Table.Td>{entry.timestamp ?? "-"}</Table.Td>
					<Table.Td>{entry.type ?? "EVENT"}</Table.Td>
					<Table.Td>
						{JSON.stringify(entry.attr || entry.details || {})}
					</Table.Td>
				</Table.Tr>
			)),
		[eventLog],
	);

	return (
		<>
			{isCompleted && result ? (
				<Card withBorder p="lg">
					<Title order={3} mb="sm">
						Run summary
					</Title>
					<Table>
						<Table.Tbody>
							{Object.entries(result).map(([key, value]) => (
								<Table.Tr key={key}>
									<Table.Td>{key}</Table.Td>
									<Table.Td>{String(value)}</Table.Td>
								</Table.Tr>
							))}
						</Table.Tbody>
					</Table>
				</Card>
			) : null}

			{isCompleted ? (
				<Card withBorder p="lg">
					<Group justify="space-between" mb="sm">
						<Title order={3}>Event log</Title>
						<Button onClick={loadMoreEvents} loading={loadingEvents}>
							Load more
						</Button>
					</Group>

					<Table striped>
						<Table.Thead>
							<Table.Tr>
								<Table.Th>Timestamp</Table.Th>
								<Table.Th>Type</Table.Th>
								<Table.Th>Details</Table.Th>
							</Table.Tr>
						</Table.Thead>
						<Table.Tbody>{rows}</Table.Tbody>
					</Table>

					{eventError ? (
						<Text c="red" mt="sm">
							{eventError}
						</Text>
					) : null}
				</Card>
			) : null}
		</>
	);
}
