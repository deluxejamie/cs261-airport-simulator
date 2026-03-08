import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";

import { MantineProvider } from "@mantine/core";
import { ConfigProvider } from "@/app/hooks";
import RunwayConfigurationSection from "./runway-configuration-section";

function renderWithProviders(ui) {
	return render(
		<MantineProvider>
			<ConfigProvider>{ui}</ConfigProvider>
		</MantineProvider>,
	);
}

describe("RunwayConfigurationSection", () => {
	it('renders the section title "Basic runway configuration"', () => {
		renderWithProviders(<RunwayConfigurationSection />);
		expect(
			screen.getByText("Basic runway configuration"),
		).toBeInTheDocument();
	});

	it('shows empty state message "No runways configured yet." when no runways exist', () => {
		renderWithProviders(<RunwayConfigurationSection />);
		expect(
			screen.getByText("No runways configured yet."),
		).toBeInTheDocument();
	});

	it('clicking "Apply runway count" with default value 1 adds a runway row to the table', async () => {
		renderWithProviders(<RunwayConfigurationSection />);

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		expect(
			screen.queryByText("No runways configured yet."),
		).not.toBeInTheDocument();
		expect(screen.getByRole("button", { name: /remove/i })).toBeInTheDocument();
	});

	it('clicking "Apply runway count" with value 2 adds 2 runway rows to the table', async () => {
		renderWithProviders(<RunwayConfigurationSection />);

		const numberInput = screen.getByLabelText("Number of runways");
		fireEvent.change(numberInput, { target: { value: "2" } });

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		const removeButtons = screen.getAllByRole("button", { name: /remove/i });
		expect(removeButtons).toHaveLength(2);
	});

	it('runway row shows mode Select defaulting to "Both (mixed mode)"', async () => {
		renderWithProviders(<RunwayConfigurationSection />);

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		expect(screen.getByText("Both (mixed mode)")).toBeInTheDocument();
	});

	it("clicking Remove button on a runway row removes it from the table", async () => {
		renderWithProviders(<RunwayConfigurationSection />);

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		expect(screen.getByRole("button", { name: /remove/i })).toBeInTheDocument();

		await act(async () => {
			fireEvent.click(screen.getByRole("button", { name: /remove/i }));
		});

		expect(
			screen.getByText("No runways configured yet."),
		).toBeInTheDocument();
	});

	it("shows an Alert with error message for invalid runway count (0)", async () => {
		renderWithProviders(<RunwayConfigurationSection />);

		const numberInput = screen.getByLabelText("Number of runways");
		// Clear the input so value becomes empty/0 — Mantine calls onChange with '' which yields 0 after Number()
		fireEvent.change(numberInput, { target: { value: "" } });

		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		expect(screen.getByRole("alert")).toBeInTheDocument();
		expect(
			screen.getByText(/number of runways must be between 1 and 10/i),
		).toBeInTheDocument();
	});

	it("changing the mode Select updates the runway mode displayed", async () => {
		renderWithProviders(<RunwayConfigurationSection />);

		// Add a runway first
		await act(async () => {
			fireEvent.click(
				screen.getByRole("button", { name: /apply runway count/i }),
			);
		});

		// The runway mode Select has no <label>, so find it by its displayed value
		const modeInput = screen.getByDisplayValue("Both (mixed mode)");

		await act(async () => {
			fireEvent.click(modeInput);
		});

		// Mantine's dropdown stays display:none in jsdom — use hidden: true
		const takeoffOption = screen.getByRole("option", {
			name: "Takeoff only",
			hidden: true,
		});

		await act(async () => {
			fireEvent.click(takeoffOption);
		});

		expect(screen.getByDisplayValue("Takeoff only")).toBeInTheDocument();
	});
});
