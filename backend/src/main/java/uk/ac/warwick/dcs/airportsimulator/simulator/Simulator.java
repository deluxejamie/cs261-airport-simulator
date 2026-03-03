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
        return simulation.run();
    }
}