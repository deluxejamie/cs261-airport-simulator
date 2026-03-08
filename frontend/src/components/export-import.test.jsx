import React from "react";
import { render, screen } from "@testing-library/react";

import { MantineProvider } from "@mantine/core";
import { ConfigProvider } from "@/app/hooks";
import ExportImportButtons, { generateConfigJSON } from "./export-import";

jest.mock("@mantine/notifications", () => ({
	showNotification: jest.fn(),
}));

function renderWithProviders(ui) {
	return render(
		<MantineProvider>
			<ConfigProvider>{ui}</ConfigProvider>
		</MantineProvider>,
	);
}

const mockRunways = [{ id: 1, mode: "mixed_mode" }];
const mockAdvancedConfig = {
	maxDelayBeforeCancelled: 10,
	fuelThresholdBeforeRedirected: 10,
	timeTakenForTakeoff: 5,
	timeTakenForLanding: 5,
};
const mockFlights = new Map([
	[
		"EASYJET-1",
		{
			callsign: "EASYJET-1",
			type: "departure",
			expected_departure_time: 0,
		},
	],
]);
const mockHazards = new Map([
	[1, { id: 1, type: "runway_closure" }],
]);

describe("generateConfigJSON", () => {
	it("returns a JSON string", () => {
		const result = generateConfigJSON(
			mockRunways,
			mockAdvancedConfig,
			mockFlights,
			mockHazards,
		);
		expect(typeof result).toBe("string");
	});

	it("output includes all four top-level keys: runways, advancedConfig, flights, hazards", () => {
		const result = JSON.parse(
			generateConfigJSON(
				mockRunways,
				mockAdvancedConfig,
				mockFlights,
				mockHazards,
			),
		);
		expect(result).toHaveProperty("runways");
		expect(result).toHaveProperty("advancedConfig");
		expect(result).toHaveProperty("flights");
		expect(result).toHaveProperty("hazards");
	});

	it("flights in the output is an Array (converted from Map)", () => {
		const result = JSON.parse(
			generateConfigJSON(
				mockRunways,
				mockAdvancedConfig,
				mockFlights,
				mockHazards,
			),
		);
		expect(Array.isArray(result.flights)).toBe(true);
		expect(result.flights).toHaveLength(1);
	});

	it("hazards in the output is an Array (converted from Map)", () => {
		const result = JSON.parse(
			generateConfigJSON(
				mockRunways,
				mockAdvancedConfig,
				mockFlights,
				mockHazards,
			),
		);
		expect(Array.isArray(result.hazards)).toBe(true);
		expect(result.hazards).toHaveLength(1);
	});

	it("passing stringify args [null, 2] produces pretty-printed JSON", () => {
		const result = generateConfigJSON(
			mockRunways,
			mockAdvancedConfig,
			mockFlights,
			mockHazards,
			[null, 2],
		);
		expect(result).toContain("\n");
		expect(result).toContain("  ");
	});
});

describe("ExportImportButtons", () => {
	it('renders "Import Config" and "Export Config" buttons', () => {
		renderWithProviders(<ExportImportButtons />);
		expect(
			screen.getByRole("button", { name: /import config/i }),
		).toBeInTheDocument();
		expect(
			screen.getByRole("button", { name: /export config/i }),
		).toBeInTheDocument();
	});
});
