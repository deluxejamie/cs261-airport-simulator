"use client";

import { useEffect, useState } from "react";
import { Badge, Button, Group, Stack, Text, Title } from "@mantine/core";
import {
	getSimulationEventLog,
	getSimulationResult,
	getSimulationStatus,
	sleep,
} from "@/lib/simulation-api";
import SimulationOutcomeFoundPage from "./simulation-outcome-viewer";
import { useRouter } from "next/navigation";
import { showNotification } from "@mantine/notifications";
import { notificationErrorOptions } from "./export-import";
import { IconCircleArrowLeft } from "@tabler/icons-react";

const POLL_EXPONENTIAL_RATE = 1.5;
const MAX_FAILED_ATTEMPTS = 2;

const BADGE_COLORS = {
	in_progress: "yellow",
	unavailable: "red",
	complete: "green",
};

export default function SimulationOutcomeView({ uuid }) {
	const [status, setStatus] = useState("in_progress");
	// This is now handled on the server side (in the simulation/[sim_id]/page.js file)
	// const router = useRouter();
	// useEffect(() => {
	// 	if (uuid.length != UUID_LENGTH) {
	// 		// uuids generated are all
	// 		showNotification({
	// 			...notificationErrorOptions,
	// 			autoClose: 5000,
	// 			message:
	// 				"Invalid simulation ID found. Redirecting to configuration portal.",
	// 		});

	// 		router.push("/");
	// 	}
	// }, [router, uuid]);

	useEffect(() => {
		let currentDelay = 1000; // delay in ms between each request
		let failedAccessAttempts = 0;
		let finished = false;
		(async () => {
			while (failedAccessAttempts < MAX_FAILED_ATTEMPTS && !finished) {
				const currentStatus = await getSimulationStatus(uuid);
				if (currentStatus == "unavailable") failedAccessAttempts++;
				else {
					setStatus(currentStatus);
					if (currentStatus == "complete") finished = true;
					else {
						await sleep(currentDelay);
						currentDelay *= POLL_EXPONENTIAL_RATE;
					}
				}
			}
			if (failedAccessAttempts == MAX_FAILED_ATTEMPTS) {
				setStatus("unavailable");
			}
		})();
	}, [uuid]);

	return (
		<Stack maw={1000} mx="auto" p="xl" gap="lg">
			<Stack p="xs">
				<Group justify="space-between" pb="0">
					<Title order={1}>Simulation Outcome</Title>
					<Button
						component="a"
						href="/"
						target="_blank"
						leftSection={<IconCircleArrowLeft />}
						variant="light"
					>
						Simulation page
					</Button>
				</Group>
				<Group justify="flex-start">
					<Text c="dimmed">Simulation ID: {uuid}</Text>
					<Badge component="span" color={BADGE_COLORS[status]} size="sm">
						{status}
					</Badge>
				</Group>
			</Stack>

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

/**
 * @returns A react component to be displayed when the simulation is currently running.
 */
const LoadingComponent = () => {
	// todo: implement loading component (SCRUM-35)
	// should tell the user that the simulation is currently loading
	// (the page will refresh from displaying this component automatically when it's ready)
	// todo for yasvi
	return <></>;
};

/**
 * @returns A react component to be displayed when a simulation is not found
 */
const SimulationNotFound = () => {
	// todo: implement a simulation not found component (SCRUM-38)
	// should tell the user that the simulation has not been found in our system and redirect them to create one by sending them to index page
	// todo for yasvi
	return <></>;
};
