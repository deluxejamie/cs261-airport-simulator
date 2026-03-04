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
  Title,
} from "@mantine/core";
import { RunwayModes, useRunways } from "../app/hooks";
const RUNWAY_MODE_OPTIONS = [
    { value: RunwayModes.MIXED_MODE, label: "Both (mixed mode)" },
    { value: RunwayModes.TAKEOFF_ONLY, label: "Takeoff only" },
    { value: RunwayModes.LANDING_ONLY, label: "Landing only" },
  ];
  
  export default function RunwayConfigurationSection({ onRunwaysChange }) {
    const { runways, addRunway, removeRunway, importRunways } = useRunways();
    const [desiredRunwayCount, setDesiredRunwayCount] = useState(1);
    const [errorMessage, setErrorMessage] = useState("");
  
    useEffect(() => {
      onRunwaysChange?.(runways);
    }, [runways, onRunwaysChange]);
  
    const handleApplyRunwayCount = () => {
      setErrorMessage("");
      try {
        const target = Number(desiredRunwayCount ?? 0);
        if (!Number.isInteger(target) || target < 1 || target > 10) {
          throw new Error("Number of runways must be between 1 and 10");
        }
  
        while (runways.length < target) {
          addRunway(RunwayModes.MIXED_MODE);
        }
        while (runways.length > target) {
          const last = runways[runways.length - 1];
          removeRunway(last.id);
        }
      } catch (error) {
        setErrorMessage(error.message);
      }
    };
  
    const handleModeChange = (id, mode) => {
      setErrorMessage("");
      try {
        const nextRunways = runways.map((runway) =>
          runway.id === id
            ? { ...runway, mode: mode || RunwayModes.MIXED_MODE }
            : runway,
        );
        importRunways(nextRunways);
      } catch (error) {
        setErrorMessage(error.message);
      }
    };
  
    const handleRemove = (id) => {
      setErrorMessage("");
      try {
        removeRunway(id);
      } catch (error) {
        setErrorMessage(error.message);
      }
    };
  
    return (
      <Card withBorder radius="md" p="lg">
        <Stack>
          <Title order={3}>Basic runway configuration</Title>
  
          <Group align="end">
            <NumberInput
              label="Number of runways"
              min={1}
              max={10}
              value={desiredRunwayCount}
              onChange={(value) => setDesiredRunwayCount(Number(value ?? 1))}
            />
            <Button onClick={handleApplyRunwayCount}>Apply runway count</Button>
          </Group>
  
          <Text size="sm" c="dimmed">
            Configure each runway flight mode: takeoff only, landing only, or both.
          </Text>
  
          {errorMessage ? <Alert color="red">{errorMessage}</Alert> : null}
  
          <Table striped withTableBorder>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Runway #</Table.Th>
                <Table.Th>Flight mode</Table.Th>
                <Table.Th />
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {runways.map((runway, index) => (
                <Table.Tr key={runway.id}>
                  <Table.Td>{index + 1}</Table.Td>
                  <Table.Td>
                    <Select
                      value={runway.mode}
                      data={RUNWAY_MODE_OPTIONS}
                      onChange={(value) => handleModeChange(runway.id, value)}
                    />
                  </Table.Td>
                  <Table.Td>
                    <Button
                      color="red"
                      variant="subtle"
                      onClick={() => handleRemove(runway.id)}
                    >
                      Remove
                    </Button>
                  </Table.Td>
                </Table.Tr>
              ))}
              {runways.length === 0 ? (
                <Table.Tr>
                  <Table.Td colSpan={3}>
                    <Text c="dimmed" ta="center">
                      No runways configured yet.
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