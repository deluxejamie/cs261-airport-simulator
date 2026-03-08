import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";

import { MantineProvider } from "@mantine/core";
import { ConfigProvider } from "@/app/hooks";
import HazardSchedulingSection from "./hazard-scheduling-section";
import RunwayConfigurationSection from "./runway-configuration-section";
import FlightSchedulingSection from "./flight-scheduling-section";

function renderWithProviders(ui) {
	return render(
		<MantineProvider>
			<ConfigProvider>{ui}</ConfigProvider>
		</MantineProvider>,
	);
}

describe("HazardSchedulingSection", () => {
	it('renders the section title "Hazards scheduling"', () => {
		renderWithProviders(<HazardSchedulingSection />);
		expect(screen.getByText("Hazards scheduling")).toBeInTheDocument();
	});

	it('"Runway closure hazard" is the default hazard type', () => {
		renderWithProviders(<HazardSchedulingSection />);
		// Use selector: 'input' to distinguish the combobox input from the listbox element
		const hazardTypeInput = screen.getByLabelText("Hazard type", {
			selector: "input",
		});
		expect(hazardTypeInput).toHaveValue("Runway closure hazard");
	});

	it("runway closure inputs (start time, duration, closure mode) are visible by default", () => {
		renderWithProviders(<HazardSchedulingSection />);
		expect(
			screen.getByLabelText(/start time \(mins from simulation start\)/i, {
				selector: "input",
			}),
		).toBeInTheDocument();
		expect(
			screen.getByLabelText(/duration \(mins\)/i, { selector: "input" }),
		).toBeInTheDocument();
		// Use selector: 'input' to avoid matching the hidden listbox element
		expect(
			screen.getByLabelText(/closure mode/i, { selector: "input" }),
		).toBeInTheDocument();
	});

	it('"Add hazard" button is disabled when no runways are configured (for runway closure)', () => {
		renderWithProviders(<HazardSchedulingSection />);
		expect(
			screen.getByRole("button", { name: /add hazard/i }),
		).toBeDisabled();
	});

	it('warning text is shown when no runways exist with runway closure type selected', () => {
		renderWithProviders(<HazardSchedulingSection />);
		expect(
			screen.getByText(
				/you need to add a runway before scheduling a runway closure hazard/i,
			),
		).toBeInTheDocument();
	});

	it('switching to "Emergency events hazard" shows the arrival callsign dropdown', async () => {
		renderWithProviders(<HazardSchedulingSection />);

		const hazardTypeInput = screen.getByLabelText("Hazard type", {
			selector: "input",
		});

		await act(async () => {
			fireEvent.click(hazardTypeInput);
		});

		// Mantine's dropdown stays display:none in jsdom — use hidden: true
		const emergencyOption = screen.getByRole("option", {
			name: /emergency events hazard/i,
			hidden: true,
		});

		await act(async () => {
			fireEvent.click(emergencyOption);
		});

		expect(
			screen.getByLabelText(/intended arrival callsign/i, {
				selector: "input",
			}),
		).toBeInTheDocument();
	});

	it('"Add hazard" button is disabled when no arrival flights exist (for emergency events)', async () => {
		renderWithProviders(<HazardSchedulingSection />);

		// Switch to emergency events hazard
		const hazardTypeInput = screen.getByLabelText("Hazard type", {
			selector: "input",
		});
		await act(async () => { fireEvent.click(hazardTypeInput); });
		await act(async () => {
			fireEvent.click(
				screen.getByRole("option", {
					name: /emergency events hazard/i,
					hidden: true,
				}),
			);
		});

		expect(
			screen.getByRole("button", { name: /add hazard/i }),
		).toBeDisabled();
	});

	it("warning text for no arrival callsigns is shown when emergency type is selected with no arrivals", async () => {
		renderWithProviders(<HazardSchedulingSection />);

		const hazardTypeInput = screen.getByLabelText("Hazard type", {
			selector: "input",
		});
		await act(async () => { fireEvent.click(hazardTypeInput); });
		await act(async () => {
			fireEvent.click(
				screen.getByRole("option", {
					name: /emergency events hazard/i,
					hidden: true,
				}),
			);
		});

		expect(
			screen.getByText(/no arrival callsigns available yet/i),
		).toBeInTheDocument();
	});

	it("with a runway added: clicking Add hazard adds a runway closure hazard row to the table", async () => {
		// Render both sections so we can add a runway via the runway section
		renderWithProviders(
			<>
				<RunwayConfigurationSection />
				<HazardSchedulingSection />
			</>,
		);

		// Add a runway
		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		// Now Add hazard should be enabled and functional
		const addHazardButton = screen.getByRole("button", { name: /add hazard/i });
		expect(addHazardButton).not.toBeDisabled();

		await act(async () => {
			fireEvent.click(addHazardButton);
		});

		// After adding a hazard there are 2 Remove buttons: one for the runway, one for the hazard
		expect(
			screen.getAllByRole("button", { name: /remove/i }),
		).toHaveLength(2);
	});

	it("Remove button on a hazard row removes it", async () => {
		renderWithProviders(
			<>
				<RunwayConfigurationSection />
				<HazardSchedulingSection />
			</>,
		);

		// Add a runway and a hazard
		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		await act(async () => {
			fireEvent.click(screen.getByRole("button", { name: /add hazard/i }));
		});

		// Two Remove buttons: runway + hazard
		expect(
			screen.getAllByRole("button", { name: /remove/i }),
		).toHaveLength(2);

		// Remove the hazard (last Remove button)
		const removeButtons = screen.getAllByRole("button", { name: /remove/i });
		await act(async () => {
			fireEvent.click(removeButtons[removeButtons.length - 1]);
		});

		// Back to 1 Remove button (only the runway remains)
		expect(
			screen.getAllByRole("button", { name: /remove/i }),
		).toHaveLength(1);
	});
});
