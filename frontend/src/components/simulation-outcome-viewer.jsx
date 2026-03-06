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
	Slider,
} from "@mantine/core";
import {
	getSimulationEventLog,
	getSimulationResult,
	getSimulationStatus,
} from "@/lib/simulation-api";
import { IconPlayerPlay } from "@tabler/icons-react";

const DEFAULT_PLAY_SPEED = 20; // 20 minutes = 1 second of playthrough

export default function SimulationOutcomeFoundPage({ uuid }) {
	return (
		<>
			<SimulationStatsComponent uuid={uuid} />
			<SimulationEventLogComponent uuid={uuid} />
		</>
	);
}

/**
 * @param {{ uuid: String}} param0 The uuid for the simulation
 * @returns A react component
 */
const SimulationEventLogComponent = ({ uuid }) => {
	// display the simulation event log (SCRUM-37)
	const [currentEvents, setCurrentEvents] = useState([]);
	const [speed, setSpeed] = useState(DEFAULT_PLAY_SPEED); // number of minutes displayed per second of playthrough
	const [running, setRunning] = useState(false);
	// References used:
	// https://mantine.dev/core/slider/
	return (
		<>
			<Title order={2}>Event Log</Title>
			<Card withBorder radius="md" p="lg">
				<Stack>
					<Group justify="space-between">
						<Title order={4}>Configure Simulation Playback</Title>
						<Text size="lg">Speed: {speed} minutes simulated/second</Text>
					</Group>
					<Slider
						color="blue"
						pb="xl"
						domain={[0, 100]}
						min={10}
						max={100}
						defaultValue={DEFAULT_PLAY_SPEED}
						onChangeEnd={setSpeed}
						disabled={running}
						marks={[
							{ value: 10, label: "10" },
							{ value: 40, label: "40" },
							{ value: 70, label: "70" },
							{ value: 100, label: "100" },
						]}
					/>
					<Button
						fullWidth
						variant="light"
						leftSection={<IconPlayerPlay size={18} />}
					>
						Start Event Simulation
					</Button>
				</Stack>
			</Card>
		</>
	);
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
