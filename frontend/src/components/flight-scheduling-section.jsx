"use client";

import { useContext, useEffect, useState } from "react";
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
	Checkbox,
} from "@mantine/core";
import { ConfigContext, EmergencyStatus, FlightType } from "../app/hooks";

const formatEmergencyLabel = (value) =>
	value
		.replace(/_/g, " ")
		.split(" ")
		.map((part) => part.charAt(0).toUpperCase() + part.slice(1))
		.join(" ");

export default function FlightSchedulingSection() {
	const { flights, addArrivalFlight, addDepartureFlight, removeFlight } =
		useContext(ConfigContext);

	const [flightType, setFlightType] = useState(FlightType.DEPARTURE);
	const [operator, setOperator] = useState("");
	const [expectedTimeMinutes, setExpectedTimeMinutes] = useState(0);
	const [arrivalFuelMinutes, setArrivalFuelMinutes] = useState(30);
	const [arrivalEmergencyStatus, setArrivalEmergencyStatus] = useState(
		EmergencyStatus.NONE,
	);
	const [errorMessage, setErrorMessage] = useState("");
	const [isRepeating, setIsRepeating] = useState(false);
	const [repeatPeriod, setRepeatPeriod] = useState(10);
	const [repeatEnd, setRepeatEnd] = useState(60);

	const clearInputs = () => {
		setOperator("");
		setExpectedTimeMinutes(0);
	};

	const handleAddFlight = () => {
		setErrorMessage("");

		try {
			if (flightType === FlightType.DEPARTURE) {
				addDepartureFlight(operator, expectedTimeMinutes);
			} else {
				addArrivalFlight(
					operator,
					arrivalEmergencyStatus,
					arrivalFuelMinutes,
					expectedTimeMinutes,
				);
			}
			clearInputs();
		} catch (error) {
			setErrorMessage(error.message);
		}
	};

	const handleRemoveFlight = (callsign) => {
		setErrorMessage("");
		try {
			removeFlight(callsign);
		} catch (error) {
			setErrorMessage(error.message);
		}
	};

	return (
		<Card withBorder radius="md" p="lg">
			<Stack>
				<Title order={3}>Arrival and departure scheduling</Title>

				<Group grow align="end">
					<Select
						label="Flight schedule type"
						value={flightType}
						onChange={(value) => setFlightType(value || FlightType.DEPARTURE)}
						data={[
							{ label: "Departure", value: FlightType.DEPARTURE },
							{ label: "Arrival", value: FlightType.ARRIVAL },
						]}
					/>

					<TextInput
						label="Aircraft operator"
						placeholder="EASYJET"
						value={operator}
						onChange={(event) => setOperator(event.currentTarget.value)}
					/>

					<NumberInput
						label={
							flightType === FlightType.DEPARTURE
								? "Expected departure time (mins from simulation start)"
								: "Expected arrival time (mins from simulation start)"
						}
						min={0}
						value={expectedTimeMinutes}
						onChange={(value) => setExpectedTimeMinutes(Number(value ?? 0))}
					/>
				</Group>

				{flightType === FlightType.ARRIVAL ? (
					<Group grow align="end">
						<NumberInput
							label="Fuel at arrival into aircraft space (minutes)"
							min={1}
							value={arrivalFuelMinutes}
							onChange={(value) => setArrivalFuelMinutes(Number(value ?? 1))}
						/>

						<Select
							label="Emergency status at airspace entry"
							value={arrivalEmergencyStatus}
							onChange={(value) =>
								setArrivalEmergencyStatus(value || EmergencyStatus.NONE)
							}
							data={[
								{
									label: "None",
									value: EmergencyStatus.NONE,
								},
								{
									label: "Mechanical Failure",
									value: EmergencyStatus.MECHANICAL_FAIL,
								},
								{
									label: "Passenger Health",
									value: EmergencyStatus.PASSENGER_HEALTH,
								},
							]}
						/>
					</Group>
				) : null}

				<Text size="sm" c="dimmed">
					Callsign is generated automatically using operator + incrementing
					counter (example: EASYJET-1, BRITISH_AIRWAYS-2).
				</Text>

				{errorMessage ? <Alert color="red">{errorMessage}</Alert> : null}

				<Group justify="flex-end">
					<Button onClick={handleAddFlight}>Add scheduled flight</Button>
				</Group>

				<Table striped withTableBorder>
					<Table.Thead>
						<Table.Tr>
							<Table.Th>Callsign</Table.Th>
							<Table.Th>Type</Table.Th>
							<Table.Th>Expected time</Table.Th>
							<Table.Th>Fuel at arrival</Table.Th>
							<Table.Th>Emergency status</Table.Th>
							<Table.Th />
						</Table.Tr>
					</Table.Thead>
					<Table.Tbody>
						{[...flights.values()].map((flight) => (
							<Table.Tr key={flight.callsign}>
								<Table.Td>{flight.callsign}</Table.Td>
								<Table.Td>{flight.type.toUpperCase()}</Table.Td>
								<Table.Td>
									{flight.expected_departure_time ??
										flight.expected_arrival_time}
								</Table.Td>

								<Table.Td>
									{flight.remaining_fuel_mins
										? `${flight.remaining_fuel_mins} mins`
										: "-"}
								</Table.Td>
								<Table.Td>
									{flight.emergency_status
										? formatEmergencyLabel(flight.emergency_status)
										: "-"}
								</Table.Td>
								<Table.Td>
									<Button
										color="red"
										variant="subtle"
										onClick={() => handleRemoveFlight(flight.callsign)}
									>
										Remove
									</Button>
								</Table.Td>
							</Table.Tr>
						))}
						{flights.length === 0 ? (
							<Table.Tr>
								<Table.Td colSpan={8}>
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
