package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class SimManager {


    public SimManager(SimulationService simulationService) {
        this.executor = Executors.newFixedThreadPool(nproc());
        this.simulationService = simulationService;
        this.simulationToState = new ConcurrentHashMap<>();
    }

    public void RunSimulation(Simulation simulation, String uuid) {
        /* Sim already ran */
        if (simulationService.getResult(uuid).isPresent()) return;

        simulationToState.put(uuid, simulation);

        executor.execute(() -> {
            final SimulationResult result = simulation.run();
            simulationService.saveResult(uuid, result);
            simulationToState.remove(uuid);
        });
    }

    private int nproc() {
        return Runtime.getRuntime().availableProcessors();
    }


    final Executor executor;
    final SimulationService simulationService;
    final ConcurrentHashMap<String, Simulation> simulationToState;
}
