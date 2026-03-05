"use client";

import {
	Badge,
	Button,
	Card,
	Group,
	Stack,
	Title,
	Tooltip,
} from "@mantine/core";
import RunwayConfigurationSection from "./runway-configuration-section";
import FlightSchedulingSection from "./flight-scheduling-section";
import HazardSchedulingSection from "./hazard-scheduling-section";
import AdvancedConfigSection from "./advanced-config-section";
import ExportImportButtons, {
	generateConfigJSON,
	notificationErrorOptions,
} from "./export-import";
import { useContext } from "react";
import { ConfigContext } from "@/app/hooks";
import { showNotification } from "@mantine/notifications";
import { useRouter } from "next/navigation";
import { createSimulation } from "@/lib/simulation-api";

export default function SimulationConfigForm() {
	const { runways, advancedConfig, flights, hazards } =
		useContext(ConfigContext);

	const router = useRouter();

	return (
		<Stack maw={980} mx="auto" p="xl" gap="lg">
			<Group justify="space-between">
				<Title order={1}>Dorset Software Airport Simulator</Title>
				<ExportImportButtons />
			</Group>

			<Card withBorder radius="md" p="lg">
				<Stack>
					<Badge variant="light" w="fit-content">
						Basic runway configuration
					</Badge>

					<RunwayConfigurationSection />
				</Stack>
			</Card>

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
			<Tooltip
				label="You must configure at least one runway and schedule at least one flight"
				disabled={!(runways.length == 0 || flights.size == 0)}
			>
				<Button
					gradient={{ from: "indigo", to: "cyan", deg: 90 }}
					fullWidth
					disabled={runways.length == 0 || flights.size == 0}
					onClick={() => {
						if (runways.length == 0 || flights.size == 0) {
							showNotification({
								...notificationErrorOptions,
								message:
									"Your simulation configuration is incomplete; you must include at least one runway",
							});
							return;
						}
						const configData = generateConfigJSON(
							runways,
							advancedConfig,
							flights,
							hazards,
						);

						const simId = createSimulation(configData);
						if (simId == "request_failed") {
							showNotification({
								...notificationErrorOptions,
								message:
									"Failure to contact simulation server. Please check your intranet connectivity and try again",
							});
						} else {
							router.push(`/simulation/${simId}`);
						}
					}}
				>
					Run Simulation
				</Button>
			</Tooltip>
		</Stack>
	);
}
