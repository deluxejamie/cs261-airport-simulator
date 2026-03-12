"use client";

import { useEffect, useMemo, useState, useCallback } from "react";
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
	Switch,
	SegmentedControl,
	Center,
	Affix,
} from "@mantine/core";
import {
	getSimulationEventLog,
	getSimulationConfiguration,
	getSimulationResult,
	getSimulationStatus,
	EventTypes,
} from "@/lib/simulation-api";
import {
	IconAlertCircleFilled,
	IconAlertHexagon,
	IconArrowUpRight,
	IconBrandFlightradar24,
	IconBuildingAirport,
	IconCircleCheckFilled,
	IconClock,
	IconDownload,
	IconPlane,
	IconPlaneArrival,
	IconPlaneDeparture,
	IconPlaneOff,
	IconPlayerPlay,
	IconPointer,
	IconPointerOff,
	IconRefresh,
	IconSignRight,
} from "@tabler/icons-react";

const DEFAULT_PLAY_SPEED = 20; // 20 minutes = 1 second of playthrough
const THRESHOLD_RESIDUAL_EVENTS = 100; // the minimum number of events residual to have before more should be fetched
const EVENTS_REQUESTED_PER_BATCH = 100; // the amount of events requested per batch
const CONSECUTIVE_FAILURES_THRESHOLD = 2; // The number of consecutive failures before the simulation log will display "failure to load"

// number of minutes displayed per second
const SPEED_VALS = [
	10, 25, 40, 60, 80, 120, 150, 240, 360, 600, 840, 1080, 1440, 2880, 4320,
	5760, 14400,
];
const formatSpeedVal = (val) => {
	const hrs = Math.floor(val / 60) % 24;
	const days = Math.floor(val / (60 * 24));
	const mins = val % 60;
	return [
		days > 0 ? `${days} day${days > 1 ? "s" : ""}` : undefined,
		hrs > 0 ? `${hrs} hour${hrs > 1 ? "s" : ""}` : undefined,
		mins > 0 ? `${mins} mins` : undefined,
	]
		.filter((x) => !!x)
		.join(" ");
};

export default function SimulationOutcomeFoundPage({ uuid }) {
	return (
		<>
			<span id="sim-stats-section" />
			<SimulationStatsComponent uuid={uuid} />
			<SimulationEventLogComponent uuid={uuid} />
		</>
	);
}

const eventBadgeColor = (val, defaultColor = "blue") =>
	val ? defaultColor : "orange";

const eventDisplayers = Object.fromEntries([
	[
		EventTypes.LANDING,
		{
			icon: <IconPlaneArrival />,
			title: (attributes) => `Flight ${attributes.callsign} has landed`,
			badges: (attributes) => {
				const HOLD_WARN_THRESHOLD = 3; // minutes
				const ARRIVAL_DELAY_WARN_THRESHOLD = 2; // minutes
				return (
					<Group justify="flex-start">
						<Badge variant="light" color="blue">
							Runway {attributes.runwayNumber}
						</Badge>
						<Badge
							variant="light"
							color={eventBadgeColor(
								attributes.holdMinutes < HOLD_WARN_THRESHOLD,
								"green",
							)}
						>
							Hold time {attributes.holdMinutes} min
						</Badge>
						<Badge
							variant="light"
							color={eventBadgeColor(
								attributes.arrivalDelay < ARRIVAL_DELAY_WARN_THRESHOLD,
								"green",
							)}
						>
							{attributes.arrivalDelay >= 0
								? `${attributes.arrivalDelay} min delay`
								: "On time"}
						</Badge>
					</Group>
				);
			},
		},
	],
	[
		EventTypes.TAKEOFF,
		{
			icon: <IconPlaneDeparture />,
			title: (attributes) => `Flight ${attributes.callsign} has departed`,
			badges: (attributes) => {
				const WAIT_THRESHOLD = 4; // minutes
				const DEPARTURE_DELAY_WARN_THRESHOLD = 2; // minutes
				return (
					<Group justify="flex-start">
						<Badge variant="light" color="blue">
							Runway {attributes.runwayNumber}
						</Badge>
						<Badge
							variant="light"
							color={eventBadgeColor(
								attributes.waitMinutes < WAIT_THRESHOLD,
								"green",
							)}
						>
							Wait time {attributes.waitMinutes} min
						</Badge>
						<Badge
							variant="light"
							color={eventBadgeColor(
								attributes.departureDelay < DEPARTURE_DELAY_WARN_THRESHOLD,
								"green",
							)}
						>
							{attributes.departureDelay > 0
								? `${attributes.departureDelay} min delay`
								: "On time"}
						</Badge>
					</Group>
				);
			},
		},
	],
	[
		EventTypes.HOLDING,
		{
			icon: <IconBuildingAirport />,
			title: (attributes) =>
				`Flight ${attributes.callsign} entered holding pattern`,
			badges: (attributes) => undefined,
		},
	],
	[
		EventTypes.DIVERSION,
		{
			icon: <IconBrandFlightradar24 />,
			title: (attributes) => `Arrival flight ${attributes.callsign} diverted`,
			badges: (attributes) => {
				return (
					<Group justify="flex-start">
						<Badge variant="light" color="red">
							{attributes.reason}
						</Badge>
					</Group>
				);
			},
		},
	],
	[
		EventTypes.CANCELLATION,
		{
			icon: <IconPlaneOff />,
			title: (attributes) =>
				`Departure flight ${attributes.callsign} cancelled`,
			badges: (attributes) => {
				return (
					<Group justify="flex-start">
						<Badge variant="light" color="orange">
							Wait time {attributes.waitingMinutes} min
						</Badge>
						<Badge variant="light" color="red">
							{attributes.reason}
						</Badge>
					</Group>
				);
			},
		},
	],
	[
		EventTypes.EMERGENCY,
		{
			icon: <IconAlertHexagon />,
			title: (attributes) =>
				`Aircraft ${attributes.callsign} experiencing emergency`,
			badges: (attributes) => {
				return (
					<Group justify="flex-start">
						<Badge variant="light" color="red">
							{attributes.emergencyStatus.replace("_", " ")} emergency
						</Badge>
					</Group>
				);
			},
		},
	],

	// Runway mode event is excluded as it cannot change currently within our simulation
	// [
	// 	EventTypes.RUNWAY_MODE,
	// 	{
	// 		icon: <IconAlertHexagon />,
	// 		title: "Aircraft Emergency",
	// 	},
	// ],

	[
		EventTypes.RUNWAY_STATUS,
		{
			icon: <IconSignRight />,
			title: (attributes) =>
				`Runway ${attributes.runwayNumber} availability status changed`,
			badges: (attributes) => {
				return (
					<Group justify="flex-start">
						<Badge
							variant="light"
							color={eventBadgeColor(attributes.status == "available", "green")}
						>
							{attributes.status.replace("_", " ")}
						</Badge>
					</Group>
				);
			},
		},
	],
]);

/**
 * This function generates a Card to display in the event log using the provided event data
 * @param {{event:object}} param0 An event from the server side event log
 * @returns {} A react component
 */
const EventCard = ({ event, style }) => {
	const eventDisplayer = eventDisplayers[event.eventType];
	if (!eventDisplayer) return undefined;
	return (
		<Card
			padding="lg"
			shadow="sm"
			withBorder
			radius="md"
			style={style}
			bg="#fafafa"
		>
			<Group justify="space-between">
				<Group justify="flex-start">
					{eventDisplayer.icon}
					<Title order={5} fw={500}>
						{eventDisplayer.title(event.attributes)}
					</Title>
					{eventDisplayer.badges?.(event.attributes)}
				</Group>
				<Group justify="flex-start" gap="5">
					<Text size="sm" c="dimmed">
						<IconClock />
					</Text>
					<Text size="sm" c="dimmed">
						{event.simTimestamp}m
					</Text>
				</Group>
			</Group>
			{eventDisplayer.body?.(event.attributes)}
		</Card>
	);
};

const UnableToConnectToServerCard = ({ style }) => {
	return (
		<Card
			padding="xl"
			shadow="sm"
			withBorder
			bg="orange"
			radius="md"
			style={style}
		>
			<Group justify="center">
				<IconAlertCircleFilled color="white" />
				<Title order={3} c="white">
					Unable to connect to server
				</Title>
			</Group>
			<Group justify="center">
				<Text size="sm" c="white" fs="italic">
					An error has occured while attempting to access the simulation
					results. Please run the simulation again to try again.
				</Text>
			</Group>
		</Card>
	);
};

const SimulationCompleteCard = ({ style }) => {
	return (
		<Card
			padding="md"
			shadow="sm"
			withBorder
			bg="#d4ffd5" // light greend4ffd5
			radius="md"
			style={style}
		>
			<Group justify="space-between">
				<Group justify="center">
					<IconPlane />
					<Title order={3}>Simulation complete!</Title>
				</Group>
				<Button
					variant="subtle"
					c="dark"
					radius="md"
					rightSection={<IconArrowUpRight size="16" />}
					onClick={() => {
						const targetElem = document.getElementById("sim-stats-section");
						if (targetElem) targetElem.scrollIntoView({ behavior: "smooth" });
					}}
				>
					View Simulation Statistics
				</Button>
			</Group>
		</Card>
	);
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
	const [autoScroll, setAutoScroll] = useState(true);

	// update the time each tick
	useEffect(() => {
		if (running && !finished) {
			const intervalId = setInterval(() => {
				setCurrentTime((c) => c + speed);
			}, 1000);
			return () => clearInterval(intervalId);
		}
	}, [finished, speed, running]);

	// scrolls down to the latest event
	useEffect(() => {
		const bottom = document.getElementById("bottom-of-page");
		if (bottom && autoScroll) {
			bottom.scrollIntoView({ behavior: "smooth" });
		}
	}, [currentEvents.length, serverUnavailable, autoScroll]);

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
						<Text size="md">
							Speed: {formatSpeedVal(speed)} simulated/second
						</Text>
					</Group>
					<Slider
						color="blue"
						pb="xl"
						min={0}
						max={SPEED_VALS.length - 1}
						defaultValue={DEFAULT_PLAY_SPEED}
						onChange={(i) => {
							if (i !== null) setSpeed(SPEED_VALS[i]);
						}}
						label={() => null}
						disabled={running && !finished}
						marks={SPEED_VALS.map((x, i) => {
							return { value: i, label: null };
						})}
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
			<Stack>
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
						{eventsFromSvr.length == currentEvents.length &&
						serverUnavailable ? (
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

				{finished ? (
					<SimulationCompleteCard
						style={{
							opacity: 0,
							animationName: "fadeIn",
							animationDuration: `0.6s`,
							animationFillMode: "forwards",
							animationTimingFunction: "ease",
						}}
					/>
				) : undefined}

				<span id="bottom-of-page" />
			</Stack>
			{running ? (
				<Affix position={{ bottom: 20, right: 20 }}>
					<SegmentedControl
						size="xl"
						transitionDuration={350}
						value={autoScroll ? "enabled" : "disabled"}
						data={[
							{
								value: "enabled",
								label: (
									<Center>
										<IconPointer />
									</Center>
								),
							},
							{
								value: "disabled",
								label: (
									<Center>
										<IconPointerOff />
									</Center>
								),
							},
						]}
						onChange={(val) => {
							if (val != null) setAutoScroll(val == "enabled");
						}}
					/>
				</Affix>
			) : undefined}
		</>
	);
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
				console.log(uuid);
				const fetchedResult = await getSimulationResult(uuid);
				if (!isMounted) return;
				setResult(fetchedResult);
			} catch (e) {
				console.log(e);
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

	return (
		<Card withBorder>
			<Stack>
				<Title order={2}>Simulation statistics</Title>
				{loading ? (
					<Center>
						<Loader size="sm" />
					</Center>
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
							onClick={() => {
								setExporting(true);
								setError(null);

								try {
									const response = result.configData;
									const configString =
										typeof response === "string"
											? response
											: (response?.config ?? JSON.stringify(response, null, 2));

									const tempLink = document.createElement("a");
									tempLink.href = window.URL.createObjectURL(
										new Blob([configString]),
									);
									tempLink.setAttribute("download", `${uuid}.json`);
									document.body.appendChild(tempLink);
									tempLink.click();
									document.body.removeChild(tempLink);
								} catch (e) {
									setError("Failed to download the simulation configuration.");
								} finally {
									setExporting(false);
								}
							}}
						>
							Export configuration
						</Button>
					</>
				)}
			</Stack>
		</Card>
	);
};

const formatDecimal = (value) => {
	if (typeof value !== "number") return "-";
	return value.toFixed(2);
};
