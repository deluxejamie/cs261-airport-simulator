"use client";
import { Button, Flex } from "@mantine/core";
import { IconDownload } from "@tabler/icons-react";
import { useDisclosure } from "@mantine/hooks";

export default function ExportImportButtons({
	runways,
	advancedConfig,
	flights,
	hazards,
}) {
	const [exportLoading, { open, close }] = useDisclosure();

	return (
		<Flex gap="sm" direction="row">
			<Button variant="filled">Import</Button>
			<Button
				variant="light"
				leftSection={<IconDownload size={16} />}
				loading={exportLoading}
				onClick={() => {
					open();
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
					close();
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
