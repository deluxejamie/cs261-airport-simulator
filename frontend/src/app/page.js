"use client";

import SimulationConfigForm from "@/components/simulation-config-form";
import { ConfigProvider } from "./hooks";

export default function Home() {
	return (
		<ConfigProvider>
			<SimulationConfigForm />
		</ConfigProvider>
	);
}
