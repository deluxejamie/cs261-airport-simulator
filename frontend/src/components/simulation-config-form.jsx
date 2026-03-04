"use client";

import { useMemo, useState } from "react";
import { Badge, Card, Stack, Text, Title } from "@mantine/core";
import FlightSchedulingSection from "./flight-scheduling-section";
import HazardSchedulingSection from "./hazard-scheduling-section";
import AdvancedConfigSection from "./advanced-config-section";
import RunwayConfigurationSection from "./runway-configuration-section";
import { FlightType } from "../app/hooks";

export default function SimulationConfigForm() {
  const [flights, setFlights] = useState([]);
  const [hazards, setHazards] = useState([]);
  const [runways, setRunways] = useState([]);
  const [advancedConfig, setAdvancedConfig] = useState({
    maxDelayBeforeCancelled: 10,
    fuelThresholdBeforeRedirected: 10,
    timeTakenForTakeoff: 5,
    timeTakenForLanding: 5,
  });

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
        <Text c="dimmed">Configuration view · merged hooks version</Text>
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
            Basic runway configuration
          </Badge>

          <RunwayConfigurationSection onRunwaysChange={setRunways} />

          <Text size="sm" c="dimmed">
            Configured runways in state: {runways.length}
          </Text>
        </Stack>
      </Card>

      <Card withBorder radius="md" p="lg">
        <Stack>
          <Badge variant="light" w="fit-content">
            Advanced configuration
          </Badge>

          <AdvancedConfigSection onAdvancedConfigChange={setAdvancedConfig} />

          <Text size="sm" c="dimmed">
            Advanced config values loaded: {Object.keys(advancedConfig).length}
          </Text>
        </Stack>
      </Card>
    </Stack>
  );
}