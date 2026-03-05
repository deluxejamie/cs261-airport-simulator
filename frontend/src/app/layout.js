import "@mantine/core/styles.css";
import "@mantine/notifications/styles.css";
import "./globals.css";
import {
	ColorSchemeScript,
	MantineProvider,
	mantineHtmlProps,
} from "@mantine/core";
import { Notifications } from "@mantine/notifications";
export const metadata = {
	title: "Airport Simulator",
	description: "CS261 Group 4 Coursework for Dorset Software",
};

export default function RootLayout({ children }) {
	return (
		<html lang="en" {...mantineHtmlProps}>
			<head>
				<ColorSchemeScript />
			</head>
			<body>
				<MantineProvider>
					<Notifications />
					{children}
				</MantineProvider>
			</body>
		</html>
	);
}
