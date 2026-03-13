"use client";

import { useContext, useEffect, useState } from "react";
import {
	Accordion,
	Alert,
	Card,
	NumberInput,
	Stack,
	Text,
} from "@mantine/core";
import { ConfigContext } from "../app/hooks";

export default function AdvancedConfigSection() {
	const {
		advancedConfig,
		setMaxDelayBeforeCancelled,
		setFuelThresholdBeforeRedirected,
		setTimeTakenForTakeoff,
		setTimeTakenForLanding,
	} = useContext(ConfigContext);

	const [errorMessage, setErrorMessage] = useState("");

	const applyUpdate = (setter, value) => {
		setErrorMessage("");
		try {
			setter(Number(value ?? 0));
		} catch (error) {
			setErrorMessage(error.message);
		}
	};

	return (
		<Card withBorder radius="md" p="lg">
			<Stack>
				<Accordion defaultValue="advanced" variant="separated">
					<Accordion.Item value="advanced">
						<Accordion.Control>Advanced configuration</Accordion.Control>
						<Accordion.Panel>
							<Stack>
								<NumberInput
									label="Max waiting time before a departure is cancelled"
									min={0}
									rightSection={<span>minutes</span>}
									rightSectionWidth={70}
									value={advancedConfig.maxDelayBeforeCancelled}
									onChange={(value) => {
										if (value !== null)
											applyUpdate(setMaxDelayBeforeCancelled, value);
									}}
								/>

								<NumberInput
									label="Fuel minute threshold before an arriving aircraft must be diverted"
									min={0}
									rightSection={<span>minutes</span>}
									rightSectionWidth={70}
									value={advancedConfig.fuelThresholdBeforeRedirected}
									onChange={(value) => {
										if (value !== null)
											applyUpdate(setFuelThresholdBeforeRedirected, value);
									}}
								/>

								<NumberInput
									label="Time taken for an aircraft to take off"
									min={1}
									rightSection={<span>minutes</span>}
									rightSectionWidth={70}
									value={advancedConfig.timeTakenForTakeoff}
									onChange={(value) => {
										if (value !== null)
											applyUpdate(setTimeTakenForTakeoff, value);
									}}
								/>

								<NumberInput
									label="Time taken for an arriving aircraft to land"
									min={1}
									rightSection={<span>minutes</span>}
									rightSectionWidth={70}
									value={advancedConfig.timeTakenForLanding}
									onChange={(value) => {
										if (value !== null)
											applyUpdate(setTimeTakenForLanding, value);
									}}
								/>

								<Text size="sm" c="dimmed">
									Modify advanced configuration parameters to provide more
									realistic results at different airports
								</Text>
							</Stack>
						</Accordion.Panel>
					</Accordion.Item>
				</Accordion>

				{errorMessage ? <Alert color="red">{errorMessage}</Alert> : null}
			</Stack>
		</Card>
	);
}
