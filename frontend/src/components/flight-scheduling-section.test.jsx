import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";

import { MantineProvider } from "@mantine/core";
import { ConfigProvider } from "@/app/hooks";
import FlightSchedulingSection from "./flight-scheduling-section";

function renderWithProviders(ui) {
	return render(
		<MantineProvider>
			<ConfigProvider>{ui}</ConfigProvider>
		</MantineProvider>,
	);
}

async function addDepartureFlight(operator = "EASYJET") {
	const operatorInput = screen.getByPlaceholderText("EASYJET");
	fireEvent.change(operatorInput, { target: { value: operator } });
	await act(async () => {
		fireEvent.click(
			screen.getByRole("button", { name: /add scheduled flight/i }),
		);
	});
}

describe("FlightSchedulingSection", () => {
	it('renders the section title "Arrival and departure scheduling"', () => {
		renderWithProviders(<FlightSchedulingSection />);
		expect(
			screen.getByText("Arrival and departure scheduling"),
		).toBeInTheDocument();
	});

	it("departure flight type is selected by default", () => {
		renderWithProviders(<FlightSchedulingSection />);
		// Mantine Select input (not the listbox) is found via label + selector: 'input'
		const input = screen.getByLabelText("Flight schedule type", {
			selector: "input",
		});
		expect(input).toHaveValue("Departure");
	});

	it('arrival-specific fields are hidden when type is "Departure"', () => {
		renderWithProviders(<FlightSchedulingSection />);
		expect(
			screen.queryByLabelText(
				/fuel at arrival into aircraft space/i,
			),
		).not.toBeInTheDocument();
		expect(
			screen.queryByLabelText(/emergency status at airspace entry/i),
		).not.toBeInTheDocument();
	});

	it('switching to "Arrival" flight type shows fuel and emergency status inputs', async () => {
		renderWithProviders(<FlightSchedulingSection />);

		const flightTypeInput = screen.getByLabelText("Flight schedule type", {
			selector: "input",
		});

		await act(async () => {
			fireEvent.click(flightTypeInput);
		});

		// Mantine's dropdown stays display:none in jsdom (no layout engine), so use hidden: true
		const arrivalOption = screen.getByRole("option", {
			name: "Arrival",
			hidden: true,
		});

		await act(async () => {
			fireEvent.click(arrivalOption);
		});

		expect(
			screen.getByLabelText(/fuel at arrival into aircraft space/i, {
				selector: "input",
			}),
		).toBeInTheDocument();
		expect(
			screen.getByLabelText(/emergency status at airspace entry/i, {
				selector: "input",
			}),
		).toBeInTheDocument();
	});

	it('clicking "Add scheduled flight" with operator and time adds a row to the flights table', async () => {
		renderWithProviders(<FlightSchedulingSection />);
		await addDepartureFlight("EASYJET");
		expect(screen.getByText("EASYJET-1")).toBeInTheDocument();
	});

	it('added departure flight row displays type "DEPARTURE"', async () => {
		renderWithProviders(<FlightSchedulingSection />);
		await addDepartureFlight("EASYJET");
		expect(screen.getByText("DEPARTURE")).toBeInTheDocument();
	});

	it('added arrival flight row displays type "ARRIVAL" and shows fuel minutes', async () => {
		renderWithProviders(<FlightSchedulingSection />);

		// Switch to Arrival
		const flightTypeInput = screen.getByLabelText("Flight schedule type", {
			selector: "input",
		});
		await act(async () => { fireEvent.click(flightTypeInput); });
		await act(async () => {
			fireEvent.click(
				screen.getByRole("option", { name: "Arrival", hidden: true }),
			);
		});

		// Add flight
		const operatorInput = screen.getByPlaceholderText("EASYJET");
		fireEvent.change(operatorInput, { target: { value: "BRITISH" } });
		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /add scheduled flight/i }),
			);
		});

		expect(screen.getByText("ARRIVAL")).toBeInTheDocument();
		// Default fuel is 30 mins
		expect(screen.getByText("30 mins")).toBeInTheDocument();
	});

	it("Remove button on a flight row removes it", async () => {
		renderWithProviders(<FlightSchedulingSection />);
		await addDepartureFlight("EASYJET");

		expect(screen.getByText("EASYJET-1")).toBeInTheDocument();

		await act(async () => {
			fireEvent.click(screen.getByRole("button", { name: /remove/i }));
		});

		expect(screen.queryByText("EASYJET-1")).not.toBeInTheDocument();
	});

	it('"Repeating flight?" checkbox, when checked, reveals "Repeat every" and "Repeat until time" inputs', async () => {
		renderWithProviders(<FlightSchedulingSection />);

		expect(
			screen.queryByLabelText(/repeat every/i),
		).not.toBeInTheDocument();

		await act(async () => {
			fireEvent.click(screen.getByLabelText(/repeating flight/i));
		});

		expect(screen.getByLabelText(/repeat every/i)).toBeInTheDocument();
		expect(screen.getByLabelText(/repeat until time/i)).toBeInTheDocument();
	});

	it("shows error Alert when repeat period is invalid (<= 0)", async () => {
		renderWithProviders(<FlightSchedulingSection />);

		// Enable repeating
		await act(async () => {
			fireEvent.click(screen.getByLabelText(/repeating flight/i));
		});

		// Set repeat period to 0 (invalid — must be > 0)
		const repeatPeriodInput = screen.getByLabelText(/repeat every/i);
		fireEvent.change(repeatPeriodInput, { target: { value: "0" } });

		// Set operator so flight validation passes
		fireEvent.change(screen.getByPlaceholderText("EASYJET"), {
			target: { value: "EASYJET" },
		});

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /add scheduled flight/i }),
			);
		});

		expect(screen.getByRole("alert")).toBeInTheDocument();
		expect(
			screen.getByText(/repeat period must be greater than 0/i),
		).toBeInTheDocument();
	});
});
