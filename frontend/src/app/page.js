"use client";
import React from "react";
import { useRunways, RunwayModes, useAdvancedConfig } from "./hooks";

export default function Home() {
	const { runways, addRunway, removeRunway } = useRunways();
	const { advancedConfig } = useAdvancedConfig();

	return <></>;
}
