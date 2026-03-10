import SimulationOutcomeFoundPage from "@/components/simulation-outcome-viewer";
import { Stack } from "@mantine/core";

export default function TestPage() {
	return (
		<Stack maw={1000} mx="auto" p="xl" gap="lg">
			<SimulationOutcomeFoundPage uuid={"123123123123"} />
		</Stack>
	);
}
