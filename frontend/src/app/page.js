"use client";
import React, { useContext, useEffect } from "react";
import { ConfigContext, RunwayModes } from "./hooks";
import ExportImportButtons from "@/components/export-import";

export default function Home() {
	const { runways, flights, hazards, advancedConfig } =
		useContext(ConfigContext);
	return <ExportImportButtons />;
}
