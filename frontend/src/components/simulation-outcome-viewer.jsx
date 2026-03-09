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
 * This function generates a Card to display in the event log using the provided event data
 * @param {object} event An event which
 * @returns {} A react component
 */
const generateEventCard = (event) => {
	return <>temp card</>;
};

/**
 * @param {{ uuid: String}} param0 The uuid for the simulation
 * @returns A react component
 */
const SimulationEventLogComponent = ({ uuid }) => {
	// display the simulation event log (SCRUM-37)
	const [eventsFromSvr, setEventsFromSvr] = useState([
		{ id: 1, time: 0 },
		{ id: 2, time: 0 },
		{ id: 3, time: 0 },
		{ id: 4, time: 25 },
	]);
	const [speed, setSpeed] = useState(DEFAULT_PLAY_SPEED); // number of minutes displayed per second of playthrough
	const [running, setRunning] = useState(false);
	const [currentTime, setCurrentTime] = useState(0);
	// temporarily 4, should be updated based on reqs to the eventlog endpoint
	const [totalEvents, setTotalEvents] = useState(4);
	const [indexSeenUntil, setIndexSeenUntil] = useState(-1);
	const currentEvents = useMemo(
		() => eventsFromSvr.filter((e) => e.time <= currentTime),
		[currentTime, eventsFromSvr],
	);
	const finished = currentEvents.length == totalEvents;

	// update the time each tick
	useEffect(() => {
		if (running && !finished) {
			const intervalId = setInterval(() => {
				setCurrentTime((c) => c + speed);
			}, 1000);
			return () => clearInterval(intervalId);
		}
	}, [finished, speed, running]);

	// updates index seen up until this current tick
	useEffect(() => {
		if (running)
			// note that 600 has been chosen as it renders them within 0.4s (400ms) so this gives plenty of time to be
			// processed in the event loop after they have fully rendered
			setTimeout(() => setIndexSeenUntil(currentEvents.length - 1), 600);
	}, [currentEvents.length, running]);

	// References used:
	// https://mantine.dev/ (several pages)
	// https://stackoverflow.com/questions/62462316/how-do-i-make-a-button-fade-in-with-css-keyframes

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
						onChange={setSpeed}
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
						onClick={() => setRunning(true)}
						disabled={running}
					>
						Start Event Simulation
					</Button>
				</Stack>
			</Card>
			{running ? (
				<>
					{currentEvents.map((e, i) => (
						<Card
							key={e.id}
							style={
								i > indexSeenUntil
									? {
											opacity: 0,
											animationName: "fadeIn",
											animationDuration: `0.4s`,
											animationFillMode: "forwards",
											animationTimingFunction: "ease",
										}
									: {}
							}
						>
							{generateEventCard(e)}
						</Card>
					))}
				</>
			) : (
				<></>
			)}
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
