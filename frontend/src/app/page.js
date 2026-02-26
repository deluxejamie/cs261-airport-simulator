"use client";
import React from "react";
import {
	useRunways,
	RunwayModes,
	useAdvancedConfig,
	useFlightSchedule,
} from "./hooks";

export default function Home() {
	const { runways, addRunway, removeRunway } = useRunways();
	const { advancedConfig } = useAdvancedConfig();
	const { flights } = useFlightSchedule();
	return <></>;
}
