"use client";

import { useState } from "react";
import { Badge, Card, Stack, Text, Title } from "@mantine/core";
import FlightSchedulingSection from "@/components/flight-scheduling-section";

export default function SimulationConfigForm() {
  const [flights, setFlights] = useState([]);

  return (
    <Stack maw={980} mx="auto" p="xl" gap="lg">
      <div>
        <Title order={1}>Airport Simulator</Title>
        <Text c="dimmed">Configuration view · Ticket 1 only</Text>
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
    </Stack>
  );
}