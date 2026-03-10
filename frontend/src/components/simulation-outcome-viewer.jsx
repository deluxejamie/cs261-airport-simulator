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
import { IconPlayerPlay, IconRefresh } from "@tabler/icons-react";

const DEFAULT_PLAY_SPEED = 20; // 20 minutes = 1 second of playthrough
const THRESHOLD_RESIDUAL_EVENTS = 100; // the minimum number of events residual to have before more should be fetched
const EVENTS_REQUESTED_PER_BATCH = 100; // the amount of events requested per batch
const CONSECUTIVE_FAILURES_THRESHOLD = 2; // The number of consecutive failures before the simulation log will display "failure to load"

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
 * @param {{event:object}} param0 An event from the server side event log
 * @returns {} A react component
 */
const EventCard = ({ event }) => {
	return <div>temp card</div>;
};

const UnableToConnectToServerCard = () => {
	return <div>unable to connect to server</div>;
};

/**
 * @param {{ uuid: String}} param0 The uuid for the simulation
 * @returns A react component
 */
const SimulationEventLogComponent = ({ uuid }) => {
	// display the simulation event log (SCRUM-37)
	const [eventsFromSvr, setEventsFromSvr] = useState([]);
	const [speed, setSpeed] = useState(DEFAULT_PLAY_SPEED); // number of minutes displayed per second of playthrough
	const [running, setRunning] = useState(false);
	const [currentTime, setCurrentTime] = useState(0);
	// temporarily 4, should be updated based on reqs to the eventlog endpoint
	const [totalEvents, setTotalEvents] = useState(Number.POSITIVE_INFINITY);
	const [indexSeenUntil, setIndexSeenUntil] = useState(-1);
	const currentEvents = useMemo(
		() => eventsFromSvr.filter((e) => e.simTimestamp <= currentTime),
		[currentTime, eventsFromSvr],
	);
	const finished = currentEvents.length == totalEvents;
	const [serverUnavailable, setServerUnavailable] = useState(false);

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

	// keeps the eventsFromSvr full with at least THRESHOLD_RESIDUAL_EVENTS
	useEffect(() => {
		let aborted = false;
		(async () => {
			const residualEvents = eventsFromSvr.length - currentEvents.length;
			let failedAttempts = 0;
			if (
				!serverUnavailable &&
				residualEvents < THRESHOLD_RESIDUAL_EVENTS &&
				eventsFromSvr.length < totalEvents
			) {
				while (failedAttempts < CONSECUTIVE_FAILURES_THRESHOLD) {
					const eventsData = await getSimulationEventLog(
						uuid,
						eventsFromSvr.length,
						EVENTS_REQUESTED_PER_BATCH,
					);
					if (aborted == true) break;
					if (!eventsData.success) {
						failedAttempts += 1;
						continue;
					}
					console.log(eventsData);

					setEventsFromSvr((e) => [...e, ...eventsData.events]);
					setTotalEvents(eventsData.total_events);
					break;
				}
				if (failedAttempts == CONSECUTIVE_FAILURES_THRESHOLD)
					setServerUnavailable(true);
			}
		})();
		return () => (aborted = true);
	}, [
		eventsFromSvr.length,
		currentEvents.length,
		totalEvents,
		uuid,
		serverUnavailable,
	]);

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
						leftSection={
							running ? <IconRefresh size={18} /> : <IconPlayerPlay size={18} />
						}
						onClick={() => {
							setCurrentTime(0);
							setServerUnavailable(false);
							setRunning(true);
						}}
						disabled={running && !finished}
					>
						{running ? "Restart Event Simulation" : "Start Event Simulation"}
					</Button>
				</Stack>
			</Card>
			{running ? (
				<>
					{currentEvents.map((e, i) => (
						<EventCard
							event={e}
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
						/>
					))}
					{eventsFromSvr.length == currentEvents.length && serverUnavailable ? (
						<UnableToConnectToServerCard
							style={{
								opacity: 0,
								animationName: "fadeIn",
								animationDuration: `0.4s`,
								animationFillMode: "forwards",
								animationTimingFunction: "ease",
							}}
						/>
					) : undefined}
				</>
			) : undefined}
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
