import React from "react";
import { render } from "@testing-library/react";
import { MantineProvider } from "@mantine/core";
import SimulationOutcomeFoundPage from "./simulation-outcome-viewer";

jest.mock("@/lib/simulation-api", () => ({
	getSimulationStatus: jest.fn(),
	getSimulationResult: jest.fn(),
	getSimulationEventLog: jest.fn(),
}));

function renderWithProviders(ui) {
	return render(<MantineProvider>{ui}</MantineProvider>);
}

describe("SimulationOutcomeFoundPage", () => {
	it("renders without crashing when given a uuid prop", () => {
		expect(() =>
			renderWithProviders(
				<SimulationOutcomeFoundPage uuid="test-uuid-1234" />,
			),
		).not.toThrow();
	});

	it("does not throw when uuid is an empty string", () => {
		expect(() =>
			renderWithProviders(<SimulationOutcomeFoundPage uuid="" />),
		).not.toThrow();
	});
});
