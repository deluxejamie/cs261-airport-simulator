"use client";
import { Button, FileButton, Flex } from "@mantine/core";
import {
	IconDownload,
	IconExclamationCircleFilled,
	IconSettingsFilled,
} from "@tabler/icons-react";
import { useDisclosure } from "@mantine/hooks";
import { useContext, useRef } from "react";
import { ConfigContext, objectHasProperties } from "@/app/hooks";
import { showNotification } from "@mantine/notifications";

export const notificationErrorOptions = {
	autoClose: 3000,
	color: "#ff0000",
	icon: <IconExclamationCircleFilled size={18} />,
};

export const notificationSuccessOptions = {
	autoClose: 3000,
	color: "#3283a8",
	icon: <IconSettingsFilled size={18} />,
};

export default function ExportImportButtons() {
	const {
		runways,
		advancedConfig,
		flights,
		hazards,
		importRunways,
		resetRunways,
		setMaxDelayBeforeCancelled,
		setFuelThresholdBeforeRedirected,
		setTimeTakenForTakeoff,
		setTimeTakenForLanding,
		resetAdvancedConfig,
		importFlightSchedule,
		resetFlightSchedule,
		importHazardSchedule,
		resetHazardSchedule,
	} = useContext(ConfigContext);

	const [importLoading, { open: importLoadStart, close: importLoadStop }] =
		useDisclosure();
	const [exportLoading, { open: exportLoadStart, close: exportLoadStop }] =
		useDisclosure();

	const resetRef = useRef(null);
	return (
		<Flex gap="sm" direction="row">
			<FileButton
				accept="application/json"
				resetRef={resetRef}
				onChange={(file) => {
					importLoadStart();
					try {
						// See reference: https://developer.mozilla.org/en-US/docs/Web/API/FileReader
						const fileReader = new FileReader();
						fileReader.onload = () => {
							try {
								const data = JSON.parse(fileReader.result);
								console.log(data);

								// reset all existing config
								resetRunways();
								resetFlightSchedule();
								resetHazardSchedule();
								resetAdvancedConfig();

								if (
									!objectHasProperties(
										data,
										"advancedConfig",
										"runways",
										"flights",
										"hazards",
									)
								)
									throw Error("Missing required configuration file attributes");

								// import advanced config
								setMaxDelayBeforeCancelled(
									data.advancedConfig.maxDelayBeforeCancelled,
								);
								setFuelThresholdBeforeRedirected(
									data.advancedConfig.fuelThresholdBeforeRedirected,
								);
								setTimeTakenForLanding(data.advancedConfig.timeTakenForLanding);
								setTimeTakenForTakeoff(data.advancedConfig.timeTakenForTakeoff);

								// import runway config
								if (!(data.runways instanceof Array))
									throw Error("Runways is not an array");
								importRunways(data.runways);

								// import flight schedule
								if (!(data.flights instanceof Array))
									throw Error("Flights is not an array");

								importFlightSchedule(data.flights);

								// import hazard schedule
								if (!(data.hazards instanceof Array))
									throw Error("Hazards is not an array");
								importHazardSchedule(data.hazards, flights, runways);

								showNotification({
									...notificationSuccessOptions,
									message: "The configuration has been imported successfully.",
								});
							} catch (e) {
								// remove the partially valid configuration, reset to default state
								resetRunways();
								resetFlightSchedule();
								resetHazardSchedule();
								resetAdvancedConfig();
								console.log(e);
								showNotification({
									...notificationErrorOptions,
									message:
										"The provided configuration file was invalid. Please upload a valid configuration file or contact your system administrator.",
								});
							}

							importLoadStop();
						};
						fileReader.readAsText(file);
					} catch (e) {
						console.log(e);
						showNotification({
							...notificationErrorOptions,
							message:
								"Unable to parse configuration file. Please select a valid file or contact your system administrator.",
						});
						importLoadStop();
					}
					resetRef.current?.();
				}}
			>
				{(props) => (
					<Button {...props} loading={importLoading}>
						Import Config
					</Button>
				)}
			</FileButton>
			<Button
				variant="light"
				leftSection={<IconDownload size={16} />}
				loading={exportLoading}
				onClick={() => {
					exportLoadStart();
					const jsonConfig = generateConfigJSON(
						runways,
						advancedConfig,
						flights,
						hazards,
						[null, 2], // this means that the json file is easier to read (has spacing for humans)
					);

					// Follows a common approach of rendering a button temporarily to download the file
					// https://www.geeksforgeeks.org/reactjs/how-to-implement-file-download-in-nextjs-using-an-api-route/
					const tempLink = document.createElement("a");
					tempLink.href = window.URL.createObjectURL(new Blob([jsonConfig]));
					tempLink.setAttribute("download", "flight_configuration.json");
					document.body.appendChild(tempLink);
					tempLink.click();
					document.body.removeChild(tempLink);
					exportLoadStop();
				}}
			>
				Export Config
			</Button>
		</Flex>
	);
}

/**
 * This function generates a JSON for the configuration currently stored by the hooks
 * @param {Array<>} runways A runways array (typedef in hooks.js)
 * @param {Object} advancedConfig An advancedConfig object (typedef in hooks.js)
 * @param {Map<>} flights A flights schedule object (typedef in hooks.js)
 * @param {Map<>} hazards A hazard schedule object (typedef in hooks.js)
 * @param {Array} args Args to provide to the JSON.stringify call
 */
export const generateConfigJSON = (
	runways,
	advancedConfig,
	flights,
	hazards,
	args = [],
) => {
	return JSON.stringify(
		{
			runways,
			advancedConfig,
			flights: [...flights.values()],
			hazards: [...hazards.values()],
		},
		...args,
	);
};
