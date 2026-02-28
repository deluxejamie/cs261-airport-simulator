"use client";
import React from "react";
import {
	useRunways,
	RunwayModes,
	useAdvancedConfig,
	useFlightSchedule,
	useHazardSchedule,
} from "./hooks";
import ExportImportButtons from "@/components/export-import";

export default function Home() {
	const { runways, addRunway, removeRunway } = useRunways();
	const { advancedConfig } = useAdvancedConfig();
	const { flights } = useFlightSchedule();
	const { hazards } = useHazardSchedule();
	return (
		<ExportImportButtons
			runways={runways}
			advancedConfig={advancedConfig}
			flights={flights}
			hazards={hazards}
		/>
	);
}
