"use client";

import { useContext, useMemo, useState } from "react";
import { Badge, Card, Stack, Text, Title } from "@mantine/core";
import FlightSchedulingSection from "./flight-scheduling-section";
import HazardSchedulingSection from "./hazard-scheduling-section";
import AdvancedConfigSection from "./advanced-config-section";
import { ConfigContext, FlightType } from "../app/hooks";

export default function SimulationConfigForm() {
	// todo: use the hooks created in hooks.js
	// you can access them by uncommenting the following:
	// const { runways, flights, hazards, advancedConfig } = useContext(ConfigContext);
	// I've included jsdocs comments for all of the methods

	const [flights, setFlights] = useState([]);
	const [hazards, setHazards] = useState([]);

	const { advancedConfig } = useContext(ConfigContext);
	const arrivalCallsigns = useMemo(
		() =>
			flights
				.filter((flight) => flight.type === FlightType.ARRIVAL)
				.map((flight) => flight.callsign),
		[flights],
	);

	return (
		<Stack maw={980} mx="auto" p="xl" gap="lg">
			<div>
				<Title order={1}>Airport Simulator</Title>
				<Text c="dimmed">
					Configuration view · Ticket 1 + Ticket 2 + Ticket 3
				</Text>
			</div>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Arrival & departure scheduling
					</Badge>

					<FlightSchedulingSection onFlightsChange={setFlights} />

					<Text size="sm" c="dimmed">
						Scheduled flights in state: {flights.length}
					</Text>
				</Stack>
			</Card>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Hazards scheduling
					</Badge>

					<HazardSchedulingSection
						onHazardsChange={setHazards}
						arrivalCallsigns={arrivalCallsigns}
					/>

					<Text size="sm" c="dimmed">
						Scheduled hazards in state: {hazards.length}
					</Text>
				</Stack>
			</Card>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Advanced configuration
					</Badge>

					<AdvancedConfigSection />

					<Text size="sm" c="dimmed">
						Advanced config values loaded: {Object.keys(advancedConfig).length}
					</Text>
				</Stack>
			</Card>
		</Stack>
	);
}
