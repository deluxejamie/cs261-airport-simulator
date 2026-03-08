"use client";

import { useContext, useState } from "react";
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
import {
	ConfigContext,
	HazardType,
	RunwayClosureMode,
	FlightType,
} from "../app/hooks";

const formatLabel = (value) =>
	value
		.replace(/_/g, " ")
		.split(" ")
		.map((part) => part.charAt(0).toUpperCase() + part.slice(1))
		.join(" ");

export default function HazardSchedulingSection() {
	"use no memo";
	const {
		flights,
		hazards,
		addRunwayClosureHazard,
		addEmergencyEventHazard,
		removeHazard,
		runways,
	} = useContext(ConfigContext);

	const [hazardType, setHazardType] = useState(HazardType.RUNWAY_CLOSURE);

	const [startTimeMinutes, setStartTimeMinutes] = useState(0);
	const [durationMinutes, setDurationMinutes] = useState(15);
	const [affectedRunway, setAffectedRunway] = useState(1);
	console.log(affectedRunway);
	const [closureMode, setClosureMode] = useState(
		RunwayClosureMode.SNOW_CLEARANCE,
	);

	const [targetArrivalCallsign, setTargetArrivalCallsign] = useState("");
	const [errorMessage, setErrorMessage] = useState("");

	const arrivalCallsigns = [...flights.values()]
		.filter((flight) => flight.type === FlightType.ARRIVAL)
		.map((flight) => flight.callsign);

	const selectedArrivalCallsign =
		targetArrivalCallsign || arrivalCallsigns[0] || "";

	const handleAddHazard = () => {
		setErrorMessage("");
		try {
			if (hazardType === HazardType.RUNWAY_CLOSURE) {
				addRunwayClosureHazard(
					startTimeMinutes,
					durationMinutes,
					affectedRunway,
					closureMode,
				);
			} else {
				addEmergencyEventHazard(selectedArrivalCallsign);
			}
		} catch (error) {
			setErrorMessage(error.message);
		}
	};

	const handleRemoveHazard = (hazardId) => {
		setErrorMessage("");
		try {
			removeHazard(hazardId);
		} catch (error) {
			setErrorMessage(error.message);
		}
	};

	return (
		<Card withBorder radius="md" p="lg">
			<Stack>
				<Title order={3}>Hazards scheduling</Title>

				<Select
					label="Hazard type"
					value={hazardType}
					onChange={(value) =>
						setHazardType(value || HazardType.RUNWAY_CLOSURE)
					}
					data={[
						{
							label: "Runway closure hazard",
							value: HazardType.RUNWAY_CLOSURE,
						},
						{
							label: "Emergency events hazard",
							value: HazardType.EMERGENCY_EVENT,
						},
					]}
				/>

				{hazardType === HazardType.RUNWAY_CLOSURE ? (
					<Group grow align="end">
						<NumberInput
							label="Start time (mins from simulation start)"
							min={0}
							value={startTimeMinutes}
							onChange={(value) => setStartTimeMinutes(Number(value ?? 0))}
						/>

						<NumberInput
							label="Duration (mins)"
							min={1}
							value={durationMinutes}
							onChange={(value) => setDurationMinutes(Number(value ?? 1))}
						/>

						<Select
							label="Affected runway"
							value={affectedRunway.toString()}
							data={Object.values(runways).map((r) => ({
								value: r.id.toString(),
								label: `Runway ${r.id}`,
							}))}
							disabled={runways.length === 0}
							onChange={(value) => setAffectedRunway(Number(value ?? 1))}
						/>

						<Select
							label="Closure mode"
							value={closureMode}
							onChange={(value) =>
								setClosureMode(value || RunwayClosureMode.SNOW_CLEARANCE)
							}
							data={Object.values(RunwayClosureMode).map((mode) => ({
								value: mode,
								label: formatLabel(mode),
							}))}
						/>
					</Group>
				) : (
					<Select
						label="Intended arrival callsign"
						value={selectedArrivalCallsign}
						onChange={(value) => setTargetArrivalCallsign(value || "")}
						placeholder="Add at least one arrival flight first"
						data={arrivalCallsigns.map((callsign) => ({
							value: callsign,
							label: callsign,
						}))}
						disabled={arrivalCallsigns.length === 0}
					/>
				)}

				{errorMessage ? <Alert color="red">{errorMessage}</Alert> : null}

				<Group justify="flex-end">
					{runways.length === 0 && hazardType == HazardType.RUNWAY_CLOSURE ? (
						<Text size="sm" c="yellow.8">
							You need to add a runway before scheduling a runway closure
							hazard.
						</Text>
					) : null}

					{arrivalCallsigns.length === 0 &&
					hazardType === HazardType.EMERGENCY_EVENT ? (
						<Text size="sm" c="yellow.8">
							No arrival flights scheduled. Add an arrival in the scheduling
							section first.
						</Text>
					) : null}
					<Button
						onClick={handleAddHazard}
						disabled={
							(runways.length === 0 &&
								hazardType === HazardType.RUNWAY_CLOSURE) ||
							(arrivalCallsigns.length === 0 &&
								hazardType === HazardType.EMERGENCY_EVENT)
						}
					>
						Add hazard
					</Button>
				</Group>

				<Table striped withTableBorder>
					<Table.Thead>
						<Table.Tr>
							<Table.Th>ID</Table.Th>
							<Table.Th>Type</Table.Th>
							<Table.Th>Start time</Table.Th>
							<Table.Th>Duration</Table.Th>
							<Table.Th>Affected runway</Table.Th>
							<Table.Th>Closure mode</Table.Th>
							<Table.Th>Target arrival callsign</Table.Th>
							<Table.Th />
						</Table.Tr>
					</Table.Thead>
					<Table.Tbody>
						{[...hazards.values()].map((hazard) => (
							<Table.Tr key={hazard.id}>
								<Table.Td>{hazard.id}</Table.Td>
								<Table.Td>{formatLabel(hazard.type)}</Table.Td>
								<Table.Td>{hazard.start_time_mins ?? "-"}</Table.Td>
								<Table.Td>{hazard.duration_mins ?? "-"}</Table.Td>
								<Table.Td>{hazard.affected_runway ?? "-"}</Table.Td>
								<Table.Td>
									{hazard.closure_mode ? formatLabel(hazard.closure_mode) : "-"}
								</Table.Td>
								<Table.Td>{hazard.target_arrival_callsign ?? "-"}</Table.Td>
								<Table.Td>
									<Button
										color="red"
										variant="subtle"
										onClick={() => handleRemoveHazard(hazard.id)}
									>
										Remove
									</Button>
								</Table.Td>
							</Table.Tr>
						))}
						{hazards.size === 0 ? (
							<Table.Tr>
								<Table.Td colSpan={8}>
									<Text c="dimmed" ta="center">
										No hazards scheduled yet.
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
