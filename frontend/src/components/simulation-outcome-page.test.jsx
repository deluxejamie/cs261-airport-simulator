import React from "react";
import { render, screen, waitFor, act } from "@testing-library/react";
import { MantineProvider } from "@mantine/core";
import SimulationOutcomeView from "./simulation-outcome-page";
import { getSimulationStatus } from "@/lib/simulation-api";

jest.mock("@/lib/simulation-api", () => ({
	getSimulationStatus: jest.fn(),
	getSimulationResult: jest.fn(),
	getSimulationEventLog: jest.fn(),
}));

jest.mock("@mantine/notifications", () => ({
	showNotification: jest.fn(),
}));

function renderWithProviders(ui) {
	return render(<MantineProvider>{ui}</MantineProvider>);
}

const TEST_UUID = "test-uuid-1234";

describe("SimulationOutcomeView", () => {
	afterEach(() => {
		jest.useRealTimers();
	});

	it('renders heading "Simulation Outcome"', () => {
		getSimulationStatus.mockReturnValue(new Promise(() => {}));
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		expect(screen.getByText("Simulation Outcome")).toBeInTheDocument();
	});

	it("renders the uuid in the page", () => {
		getSimulationStatus.mockReturnValue(new Promise(() => {}));
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		expect(
			screen.getByText(`Simulation ID: ${TEST_UUID}`),
		).toBeInTheDocument();
	});

	it('shows "in_progress" badge text on initial render', () => {
		getSimulationStatus.mockReturnValue(new Promise(() => {}));
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		expect(screen.getByText("in_progress")).toBeInTheDocument();
	});

	it("badge is yellow on initial render", () => {
		getSimulationStatus.mockReturnValue(new Promise(() => {}));
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		const badge = screen.getByText("in_progress");
		const badgeRoot = badge.closest(".mantine-Badge-root");
		expect(badgeRoot).not.toBeNull();
		expect(badgeRoot.style.getPropertyValue("--badge-bg")).toContain(
			"yellow",
		);
	});

	it("calls getSimulationStatus with the provided uuid on mount", async () => {
		getSimulationStatus.mockResolvedValue("complete");
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		await waitFor(() =>
			expect(getSimulationStatus).toHaveBeenCalledWith(TEST_UUID),
		);
	});

	it('badge text becomes "complete" when getSimulationStatus returns "complete"', async () => {
		getSimulationStatus.mockResolvedValue("complete");
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		await waitFor(() =>
			expect(screen.getByText("complete")).toBeInTheDocument(),
		);
	});

	it("badge colour is green when status is complete", async () => {
		getSimulationStatus.mockResolvedValue("complete");
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		await waitFor(() => {
			const badge = screen.getByText("complete");
			const badgeRoot = badge.closest(".mantine-Badge-root");
			expect(badgeRoot).not.toBeNull();
			expect(badgeRoot.style.getPropertyValue("--badge-bg")).toContain(
				"green",
			);
		});
	});

	it("renders SimulationOutcomeFoundPage content without crashing when status is complete", async () => {
		getSimulationStatus.mockResolvedValue("complete");
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		await waitFor(() =>
			expect(screen.getByText("complete")).toBeInTheDocument(),
		);
		// getSimulationStatus should not be called again after finishing
		expect(getSimulationStatus).toHaveBeenCalledTimes(1);
	});

	it('badge text becomes "unavailable" after two unavailable responses', async () => {
		getSimulationStatus.mockResolvedValue("unavailable");
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		await waitFor(() =>
			expect(screen.getByText("unavailable")).toBeInTheDocument(),
		);
	});

	it("badge colour is red when status is unavailable", async () => {
		getSimulationStatus.mockResolvedValue("unavailable");
		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);
		await waitFor(() => {
			const badge = screen.getByText("unavailable");
			const badgeRoot = badge.closest(".mantine-Badge-root");
			expect(badgeRoot).not.toBeNull();
			expect(badgeRoot.style.getPropertyValue("--badge-bg")).toContain(
				"red",
			);
		});
	});

	it('badge eventually transitions to "complete" when status goes in_progress then complete', async () => {
		jest.useFakeTimers();
		getSimulationStatus
			.mockResolvedValueOnce("in_progress")
			.mockResolvedValueOnce("complete");

		renderWithProviders(<SimulationOutcomeView uuid={TEST_UUID} />);

		// Wait for the first poll (in_progress) to resolve and sleep to be scheduled
		await act(async () => {
			await Promise.resolve();
		});

		// Advance past the 1000ms sleep delay
		await act(async () => {
			jest.advanceTimersByTime(1500);
		});

		await waitFor(() =>
			expect(screen.getByText("complete")).toBeInTheDocument(),
		);
	});
});
