"use client";

import { useEffect, useState } from "react";
import {
	Alert,
	Badge,
	Button,
	Group,
	Stack,
	Text,
	Title,
	Card,
	Loader,
} from "@mantine/core";
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
const REDIRECT_DELAY_MS = 8000;

const BADGE_COLORS = {
	loading: "yellow",
	in_progress: "yellow",
	unavailable: "red",
	complete: "green",
};

export default function SimulationOutcomeView({ uuid }) {
	const [status, setStatus] = useState("loading");
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
		let cancelled = false;
		let currentDelay = 1000; // delay in ms between each request
		let failedAccessAttempts = 0;
		let finished = false;
		(async () => {
			while (
				!cancelled &&
				failedAccessAttempts < MAX_FAILED_ATTEMPTS &&
				!finished
			) {
				const currentStatus = await getSimulationStatus(uuid);
				if (cancelled) return;

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
			if (!cancelled && failedAccessAttempts == MAX_FAILED_ATTEMPTS) {
				setStatus("unavailable");
			}
		})();
		return () => {
			cancelled = true;
		};
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
						{status.replace("_", " ")}
					</Badge>
				</Group>
			</Stack>

			{status == "in_progress" ? (
				<LoadingComponent />
			) : status == "complete" ? (
				<SimulationOutcomeFoundPage uuid={uuid} />
			) : status == "unavailable" ? (
				<SimulationNotFound />
			) : undefined}
		</Stack>
	);
}

/**
 * @returns A react component to be displayed when the simulation is currently running.
 */
const LoadingComponent = () => {
	return (
		<Card withBorder>
			<Stack align="center" py="xl" gap="xs">
				<Loader size="md" />
				<Title order={3}>Simulation in progress</Title>
				<Text c="dimmed" ta="center">
					Please wait patiently while your simulation is executed. Once the
					results have been gathered, this page will automatically refresh.
				</Text>
			</Stack>
		</Card>
	);
};

/**
 * @returns A react component to be displayed when a simulation is not found
 */
const SimulationNotFound = () => {
	const router = useRouter();

	useEffect(() => {
		const redirectTimeout = setTimeout(() => {
			router.push("/");
		}, REDIRECT_DELAY_MS);

		return () => clearTimeout(redirectTimeout);
	}, [router]);

	return (
		<Card withBorder>
			<Stack gap="sm">
				<Title order={3}>Simulation not found</Title>
				<Alert color="red">
					The simulation ID appears to be invalid or unavailable. You will be
					redirected to the configuration page to run a new simulation.
				</Alert>
				<Text c="dimmed" size="sm">
					If this issue persists, please contact your system administrator.
				</Text>
				<Button fullWidth onClick={() => router.push("/")}>
					Run a new simulation
				</Button>
			</Stack>
		</Card>
	);
};
