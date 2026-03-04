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

	return (
		<Stack maw={980} mx="auto" p="xl" gap="lg">
			<div>
				<Title order={1}>Dorset Software Airport Simulator</Title>
			</div>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Arrival & departure scheduling
					</Badge>

					<FlightSchedulingSection />
				</Stack>
			</Card>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Hazards scheduling
					</Badge>

					<HazardSchedulingSection />
				</Stack>
			</Card>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Advanced configuration
					</Badge>

					<AdvancedConfigSection />
				</Stack>
			</Card>
		</Stack>
	);
}
