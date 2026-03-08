package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventType;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
        simulationToState.put(uuid, simulation);

        executor.execute(() -> {

            final SimulationResult result = simulation.run();
            simulationService.saveResult(uuid, result);

            simulationToState.remove(uuid);
        });
    }

    public Optional<List<EventLogEntry>> GetSimulationEventLog(String uuid, int offset, int count) {
        if (simulationToState.containsKey(uuid)) {
            final Simulation sim = simulationToState.get(uuid);
            return Optional.of(sim.getEventLog(offset, count));
        } else {
            final List<EventLogEntryEntity> eventLog = simulationService.getEventLog(uuid, offset, count);
            return Optional.of(eventLog.stream().map((EventLogEntryEntity og) -> new EventLogEntry(
                    EventType.valueOf(og.getEventType()),
                    og.getSimTimestamp(),
                    new ObjectMapper().readValue(og.getAttributes(), new TypeReference<>() {
                    })
            )).toList());
        }
    }

    private int nproc() {
        return Runtime.getRuntime().availableProcessors();
    }


    final Executor executor;
    final SimulationService simulationService;
    final ConcurrentHashMap<String, Simulation> simulationToState;
}
