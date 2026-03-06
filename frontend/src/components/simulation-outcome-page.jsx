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
import SimulationOutcomeFoundPage from "./simulation-outcome-viewer";

const POLL_EXPONENTIAL_RATE = 1.5;
const MAX_FAILED_ATTEMPTS = 2;

export default function SimulationOutcomeView({ uuid }) {
	const [status, setStatus] = useState("pending");
	const isCompleted = status === "completed";

	// todo: add effect which updates setStatus by polling the status endpoint with exponential backoff (use the constants above)

	return (
		<Stack maw={1000} mx="auto" p="xl" gap="lg">
			<Group justify="space-between">
				<div>
					<Title order={1}>Simulation Outcome</Title>
					<Text c="dimmed">Simulation ID: {uuid}</Text>
				</div>
				<Badge color={isCompleted ? "green" : "yellow"}>{status}</Badge>
			</Group>

			{pollError ? <Alert color="red">{pollError}</Alert> : null}

			{status == "in_progress" ? (
				<LoadingComponent />
			) : status == "complete" ? (
				<SimulationOutcomeFoundPage uuid={uuid} />
			) : (
				<SimulationNotFound />
			)}
		</Stack>
	);
}

// todo: implement loading component (SCRUM-35)
// should tell the user that the simulation is currently loading
// (the page will refresh from displaying this component automatically when it's ready)
const LoadingComponent = () => {
	return <></>;
};

// todo: implement a simulation not found component (SCRUM-38)
// should tell the user that the simulation has not been found in our system and redirect them to create one by sending them to index page
const SimulationNotFound = () => {
	return <></>;
};
