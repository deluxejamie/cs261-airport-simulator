import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";

import { MantineProvider } from "@mantine/core";
import { ConfigProvider } from "@/app/hooks";
import SimulationConfigForm from "./simulation-config-form";
import { createSimulation } from "@/lib/simulation-api";
import { showNotification } from "@mantine/notifications";

const mockPush = jest.fn();

jest.mock("next/navigation", () => ({
	useRouter: () => ({ push: mockPush }),
}));

jest.mock("@mantine/notifications", () => ({
	showNotification: jest.fn(),
}));

jest.mock("@/lib/simulation-api", () => ({
	createSimulation: jest.fn(),
}));

function renderWithProviders(ui) {
	return render(
		<MantineProvider>
			<ConfigProvider>{ui}</ConfigProvider>
		</MantineProvider>,
	);
}

describe("SimulationConfigForm", () => {
	it('renders the page title "Dorset Software Airport Simulator"', () => {
		renderWithProviders(<SimulationConfigForm />);
		expect(
			screen.getByText("Dorset Software Airport Simulator"),
		).toBeInTheDocument();
	});

	it("renders all four section badge labels", () => {
		renderWithProviders(<SimulationConfigForm />);
		// Each badge text also appears in the section's title heading — use getAllByText
		expect(
			screen.getAllByText("Basic runway configuration").length,
		).toBeGreaterThanOrEqual(1);
		// "Arrival & departure scheduling" (with &) appears only in the badge;
		// the section heading uses "Arrival and departure scheduling"
		expect(
			screen.getByText("Arrival & departure scheduling"),
		).toBeInTheDocument();
		expect(
			screen.getAllByText("Hazards scheduling").length,
		).toBeGreaterThanOrEqual(1);
		expect(
			screen.getAllByText("Advanced configuration").length,
		).toBeGreaterThanOrEqual(1);
	});

	it('"Run Simulation" button is disabled when no runways and no flights (default state)', () => {
		renderWithProviders(<SimulationConfigForm />);
		expect(
			screen.getByRole("button", { name: /run simulation/i }),
		).toBeDisabled();
	});

	it('"Run Simulation" button is disabled when runways exist but no flights', async () => {
		renderWithProviders(<SimulationConfigForm />);

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		expect(
			screen.getByRole("button", { name: /run simulation/i }),
		).toBeDisabled();
	});

	it('"Run Simulation" button is enabled when at least one runway and one flight are configured', async () => {
		renderWithProviders(<SimulationConfigForm />);

		// Add a runway (default count is 1)
		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		// Add a departure flight
		const operatorInput = screen.getByPlaceholderText("EASYJET");
		fireEvent.change(operatorInput, { target: { value: "EASYJET" } });

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /add scheduled flight/i }),
			);
		});

		expect(
			screen.getByRole("button", { name: /run simulation/i }),
		).not.toBeDisabled();
	});

	it("calls createSimulation with correct JSON and navigates via router.push on successful submission", async () => {
		createSimulation.mockResolvedValue("sim-123");

		renderWithProviders(<SimulationConfigForm />);

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		fireEvent.change(screen.getByPlaceholderText("EASYJET"), {
			target: { value: "EASYJET" },
		});

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /add scheduled flight/i }),
			);
		});

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /run simulation/i }),
			);
		});

		expect(createSimulation).toHaveBeenCalledWith(expect.any(String));
		expect(mockPush).toHaveBeenCalledWith("/simulation/sim-123");
	});

	it('shows a notification when createSimulation returns "request_failed"', async () => {
		createSimulation.mockResolvedValue("request_failed");

		renderWithProviders(<SimulationConfigForm />);

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		fireEvent.change(screen.getByPlaceholderText("EASYJET"), {
			target: { value: "EASYJET" },
		});

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /add scheduled flight/i }),
			);
		});

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /run simulation/i }),
			);
		});

		expect(showNotification).toHaveBeenCalled();
	});
});
