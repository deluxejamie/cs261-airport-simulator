"use client";
import { Button, FileButton, Flex } from "@mantine/core";
import { IconDownload, IconExclamationCircleFilled } from "@tabler/icons-react";
import { useDisclosure } from "@mantine/hooks";
import { useContext, useRef } from "react";
import { ConfigContext } from "@/app/hooks";
import { showNotification } from "@mantine/notifications";

const notificationErrorOptions = {
	autoClose: 3000,
	color: "#ff0000",
	icon: <IconExclamationCircleFilled size={18} />,
};

export default function ExportImportButtons() {
	const {
		runways,
		advancedConfig,
		flights,
		hazards,
		addRunway,
		setMaxDelayedBeforeCancelled,
		setFuelThresholdBeforeRedirected,
		setTimeTakenForTakeoff,
		setTimeTakenForLanding,
		addArrivalFlight,
		addDepartureFlight,
		addRunwayClosureHazard,
		addEmergencyEventHazard,
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
								// parse data using methods
							} catch (e) {
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
						Import
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
				Export
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
	args,
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
