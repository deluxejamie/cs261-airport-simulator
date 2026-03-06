import SimulationOutcomeView from "@/components/simulation-outcome-page";
import { redirect } from "next/navigation";

const UUID_LENGTH = 32; // uuids are 32 characters long (v4 uuid)

export default async function Simulation({ params }) {
	const { sim_id: simulationId } = await params;

	// clearly an invalid uuid. Reduces processing with redirects
	if (simulationId.length != UUID_LENGTH) {
		// uuids generated are all
		redirect("/");
	}
	return <SimulationOutcomeView uuid={simulationId} />;
}
