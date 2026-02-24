import "@mantine/core/styles.css";
import "./globals.css";
import {
	ColorSchemeScript,
	MantineProvider,
	mantineHtmlProps,
} from "@mantine/core";

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
				<MantineProvider>{children}</MantineProvider>
			</body>
		</html>
	);
}
