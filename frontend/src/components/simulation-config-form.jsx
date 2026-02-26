"use client";

import { useMemo, useState } from "react";
import { Badge, Card, Stack, Text, Title } from "@mantine/core";
import FlightSchedulingSection from "./flight-scheduling-section";
import HazardSchedulingSection from "./hazard-scheduling-section";
import AdvancedConfigSection from "./advanced-config-section";
import {
  DEFAULT_FUEL_THRESHOLD_BEFORE_DIVERSION_MINS,
  DEFAULT_LANDING_DURATION_MINS,
  DEFAULT_MAX_WAIT_BEFORE_TAKEOFF_MINS,
  DEFAULT_TAKEOFF_DURATION_MINS,
  FlightType,
} from "../app/hooks";

export default function SimulationConfigForm() {
  const [flights, setFlights] = useState([]);
  const [hazards, setHazards] = useState([]);
  const [advancedConfig, setAdvancedConfig] = useState({
    max_wait_before_takeoff_mins: DEFAULT_MAX_WAIT_BEFORE_TAKEOFF_MINS,
    fuel_threshold_before_diversion_mins:
      DEFAULT_FUEL_THRESHOLD_BEFORE_DIVERSION_MINS,
    takeoff_duration_mins: DEFAULT_TAKEOFF_DURATION_MINS,
    landing_duration_mins: DEFAULT_LANDING_DURATION_MINS,
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
        <Text c="dimmed">Configuration view · Ticket 1 + Ticket 2 + Ticket 3</Text>
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

          <AdvancedConfigSection onAdvancedConfigChange={setAdvancedConfig} />

          <Text size="sm" c="dimmed">
            Advanced config values loaded: {Object.keys(advancedConfig).length}
          </Text>
        </Stack>
      </Card>
    </Stack>
  );
}