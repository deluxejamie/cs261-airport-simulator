"use client";

import { useEffect, useState } from "react";
import {
  Accordion,
  Alert,
  Card,
  NumberInput,
  Stack,
  Text,
} from "@mantine/core";
import { useAdvancedConfig } from "../app/hooks";

export default function AdvancedConfigSection({ onAdvancedConfigChange }) {
  const {
    advancedConfig,
    setMaxWaitBeforeTakeoff,
    setFuelThresholdBeforeDiversion,
    setTakeoffDuration,
    setLandingDuration,
  } = useAdvancedConfig();

  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    onAdvancedConfigChange?.(advancedConfig);
  }, [advancedConfig, onAdvancedConfigChange]);

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
            <Accordion.Control>
              Advanced configuration (collapsible)
            </Accordion.Control>
            <Accordion.Panel>
              <Stack>
                <NumberInput
                  label="Max waiting time before takeoff (mins)"
                  min={0}
                  value={advancedConfig.max_wait_before_takeoff_mins}
                  onChange={(value) => applyUpdate(setMaxWaitBeforeTakeoff, value)}
                />

                <NumberInput
                  label="Fuel threshold before diversion (mins)"
                  min={0}
                  value={advancedConfig.fuel_threshold_before_diversion_mins}
                  onChange={(value) =>
                    applyUpdate(setFuelThresholdBeforeDiversion, value)
                  }
                />

                <NumberInput
                  label="Time taken for aircraft to take off (mins)"
                  min={1}
                  value={advancedConfig.takeoff_duration_mins}
                  onChange={(value) => applyUpdate(setTakeoffDuration, value)}
                />

                <NumberInput
                  label="Time taken for aircraft to land (mins)"
                  min={1}
                  value={advancedConfig.landing_duration_mins}
                  onChange={(value) => applyUpdate(setLandingDuration, value)}
                />

                <Text size="sm" c="dimmed">
                  Defaults are preloaded and editable.
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