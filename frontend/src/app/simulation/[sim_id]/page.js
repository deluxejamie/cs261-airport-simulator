import SimulationOutcomeView from "@/components/simulation-outcome-view";

export default async function Simulation({ params }) {
	const { sim_id: simulationId } = await params;
	return <SimulationOutcomeView uuid={simulationId} />;
}
