package uk.ac.warwick.dcs.airportsimulator.simulator;

import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

import java.util.Objects;

/**
 * Runs a Simulation until it finishes.
 *
 * Note:
 * Simulation currently provides stepTestTillScrumMerged(dt) as a temporary stepping
 * mechanism while other scrum tickets are pending.
 */
public final class Simulator {

    private final Simulation simulation;

    public Simulator(Simulation simulation) {
        this.simulation = Objects.requireNonNull(simulation, "simulation");
    }

    /**
     * Execute the simulation until completion and return a SimulationResult.
     */
    public SimulationResult run() {
        SimulationResult result = new SimulationResult();

        int safetyCap = 1_000_000; // prevent accidental infinite loops during early stage

        while (!simulation.isFinished() && safetyCap-- > 0) {
            // Advance the simulation by 1 minute using the temporary stepping function.
            simulation.stepTestTillScrumMerged(1);
        }

        result.finalizeAverages();
        return result;
    }
}