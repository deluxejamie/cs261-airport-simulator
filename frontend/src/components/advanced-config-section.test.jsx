import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";

import { MantineProvider } from "@mantine/core";
import { ConfigProvider } from "@/app/hooks";
import AdvancedConfigSection from "./advanced-config-section";

function renderWithProviders(ui) {
	return render(
		<MantineProvider>
			<ConfigProvider>{ui}</ConfigProvider>
		</MantineProvider>,
	);
}

describe("AdvancedConfigSection", () => {
	it('renders the Accordion control "Advanced configuration"', () => {
		renderWithProviders(<AdvancedConfigSection />);
		expect(screen.getByText("Advanced configuration")).toBeInTheDocument();
	});

	it("all four NumberInput fields are present", () => {
		renderWithProviders(<AdvancedConfigSection />);
		expect(
			screen.getByLabelText(
				/max waiting time before a departure is cancelled/i,
			),
		).toBeInTheDocument();
		expect(
			screen.getByLabelText(
				/fuel minute threshold before an arriving aircraft must be diverted/i,
			),
		).toBeInTheDocument();
		expect(
			screen.getByLabelText(/time taken for an aircraft to take off/i),
		).toBeInTheDocument();
		expect(
			screen.getByLabelText(/time taken for an arriving aircraft to land/i),
		).toBeInTheDocument();
	});

	it("default values match the hook defaults (10, 10, 5, 5 minutes)", () => {
		renderWithProviders(<AdvancedConfigSection />);

		expect(
			screen.getByLabelText(
				/max waiting time before a departure is cancelled/i,
			),
		).toHaveValue("10");

		expect(
			screen.getByLabelText(
				/fuel minute threshold before an arriving aircraft must be diverted/i,
			),
		).toHaveValue("10");

		expect(
			screen.getByLabelText(/time taken for an aircraft to take off/i),
		).toHaveValue("5");

		expect(
			screen.getByLabelText(/time taken for an arriving aircraft to land/i),
		).toHaveValue("5");
	});

	it('changing the "Max waiting time before a departure is cancelled" input updates the value', async () => {
		renderWithProviders(<AdvancedConfigSection />);

		const input = screen.getByLabelText(
			/max waiting time before a departure is cancelled/i,
		);

		await act(async () => {
			fireEvent.change(input, { target: { value: "20" } });
		});

		expect(input).toHaveValue("20");
	});

	it("shows an Alert error message when an invalid value is entered", async () => {
		renderWithProviders(<AdvancedConfigSection />);

		// timeTakenForTakeoff has min=1, so 0 is invalid (isPositiveInteger(0) is false)
		const takeoffInput = screen.getByLabelText(
			/time taken for an aircraft to take off/i,
		);

		await act(async () => {
			fireEvent.change(takeoffInput, { target: { value: "0" } });
		});

		expect(screen.getByRole("alert")).toBeInTheDocument();
	});
});
