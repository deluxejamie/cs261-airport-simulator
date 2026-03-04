"use client";

import { Badge, Card, Group, Stack, Title } from "@mantine/core";
import FlightSchedulingSection from "./flight-scheduling-section";
import HazardSchedulingSection from "./hazard-scheduling-section";
import AdvancedConfigSection from "./advanced-config-section";
import ExportImportButtons from "./export-import";

export default function SimulationConfigForm() {
	return (
		<Stack maw={980} mx="auto" p="xl" gap="lg">
			<Group justify="space-between">
				<Title order={1}>Dorset Software Airport Simulator</Title>
				<ExportImportButtons />
			</Group>

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
