"use client";

import { useEffect, useState } from "react";
import {
  Alert,
  Button,
  Card,
  Group,
  NumberInput,
  Select,
  Stack,
  Table,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { EmergencyStatus, FlightType, useFlightSchedule } from "@/app/hooks";

export default function FlightSchedulingSection({ onFlightsChange }) {
  const { flights, addArrivalFlight, addDepartureFlight, removeFlight } =
    useFlightSchedule();
  const [type, setType] = useState(FlightType.DEPARTURE);
  const [operator, setOperator] = useState("");
  const [expectedTime, setExpectedTime] = useState(0);
  const [fuelMins, setFuelMins] = useState(30);
  const [emergencyStatus, setEmergencyStatus] = useState(EmergencyStatus.NONE);
  const [error, setError] = useState("");

  useEffect(() => {
    onFlightsChange?.(flights);
  }, [flights, onFlightsChange]);

  const handleAdd = () => {
    setError("");
    try {
      if (type === FlightType.DEPARTURE) {
        addDepartureFlight(operator, expectedTime);
      } else {
        addArrivalFlight(
          operator,
          emergencyStatus,
          fuelMins,
          expectedTime,
          undefined,
        );
      }
      setOperator("");
      setExpectedTime(0);
    } catch (e) {
      setError(e.message);
    }
  };

  const handleRemove = (callsign) => {
    try {
      removeFlight(callsign);
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <Card withBorder radius="md" p="lg">
      <Stack>
        <Title order={3}>Arrival and departure scheduling</Title>
        <Group grow>
          <Select
            label="Flight type"
            value={type}
            onChange={(value) => setType(value || FlightType.DEPARTURE)}
            data={[
              { label: "Departure", value: FlightType.DEPARTURE },
              { label: "Arrival", value: FlightType.ARRIVAL },
            ]}
          />
          <TextInput
            label="Aircraft operator"
            value={operator}
            onChange={(event) => setOperator(event.currentTarget.value)}
            placeholder="EASYJET"
          />
          <NumberInput
            label={`Expected ${type} time (mins from sim start)`}
            min={0}
            value={expectedTime}
            onChange={(value) => setExpectedTime(Number(value || 0))}
          />
        </Group>

        {type === FlightType.ARRIVAL ? (
          <Group grow>
            <NumberInput
              label="Fuel at arrival into airspace (mins)"
              min={1}
              value={fuelMins}
              onChange={(value) => setFuelMins(Number(value || 1))}
            />
            <Select
              label="Emergency status"
              value={emergencyStatus}
              onChange={(value) => setEmergencyStatus(value || EmergencyStatus.NONE)}
              data={Object.values(EmergencyStatus).map((value) => ({
                value,
                label: value,
              }))}
            />
          </Group>
        ) : null}

        {error ? <Alert color="red">{error}</Alert> : null}

        <Group justify="flex-end">
          <Button onClick={handleAdd}>Add flight</Button>
        </Group>

        <Table striped>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>Callsign</Table.Th>
              <Table.Th>Type</Table.Th>
              <Table.Th>Expected</Table.Th>
              <Table.Th>Observed</Table.Th>
              <Table.Th>Fuel</Table.Th>
              <Table.Th>Emergency</Table.Th>
              <Table.Th />
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {flights.map((flight) => (
              <Table.Tr key={flight.callsign}>
                <Table.Td>{flight.callsign}</Table.Td>
                <Table.Td>{flight.type}</Table.Td>
                <Table.Td>
                  {flight.expected_departure_time ?? flight.expected_arrival_time}
                </Table.Td>
                <Table.Td>
                  {flight.observed_departure_time ?? flight.observed_arrival_time}
                </Table.Td>
                <Table.Td>{flight.remaining_fuel_mins ?? "-"}</Table.Td>
                <Table.Td>{flight.emergency_status ?? "-"}</Table.Td>
                <Table.Td>
                  <Button
                    color="red"
                    variant="subtle"
                    onClick={() => handleRemove(flight.callsign)}
                  >
                    Remove
                  </Button>
                </Table.Td>
              </Table.Tr>
            ))}
            {flights.length === 0 ? (
              <Table.Tr>
                <Table.Td colSpan={7}>
                  <Text c="dimmed" ta="center">
                    No flights scheduled yet.
                  </Text>
                </Table.Td>
              </Table.Tr>
            ) : null}
          </Table.Tbody>
        </Table>
      </Stack>
    </Card>
  );
}