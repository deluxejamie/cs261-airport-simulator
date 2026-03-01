package uk.ac.warwick.dcs.airportsimulator.simulator;

import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;

import java.util.Objects;

public final class Simulator {

    private final Simulation simulation;

    public Simulator(Simulation simulation) {
        this.simulation = Objects.requireNonNull(simulation, "simulation");
    }

    public SimulationResult run() {
        SimulationResult result = new SimulationResult();

        // run any events scheduled at t=0
        simulation.getEventSchedular().step(simulation.getSimTime());

        int safetyCap = 1_000_000;
        while (!simulation.isFinished() && safetyCap-- > 0) {
            simulation.setSimTime(simulation.getSimTime() + 1);
            simulation.getEventSchedular().step(simulation.getSimTime());
        }

        result.finalizeAverages();
        return result;
    }
}