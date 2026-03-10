package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.airportsimulator.entity.*;
import uk.ac.warwick.dcs.airportsimulator.repository.*;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;
import java.util.List;
import java.util.Optional;

/**
 * Service for creating simulations and retrieving persisted simulation state,
 * including initial configuration, status, results, and event logs.
 */

@Service
public class SimulationService {

    private final SimulationRepository simRepo;
    private final SimulationResultRepository resultRepo;
    private final EventLogEntryRepository logRepo;

    public SimulationService(SimulationRepository simRepo,
                              SimulationResultRepository resultRepo,
                              EventLogEntryRepository logRepo) {
        this.simRepo = simRepo;
        this.resultRepo = resultRepo;
        this.logRepo = logRepo;
    }

    /** Called when a simulation is first submitted. Saves it with RUNNING status. */
    public void createSimulation(String uuid, String configJson) {
        simRepo.save(new SimulationEntity(uuid, "RUNNING", configJson));
    }

    /** Called when the simulation finishes. Saves results and marks it COMPLETED. */
    public void saveResult(String uuid, SimulationResult result) {
        result.finalizeAverages();
        resultRepo.save(new SimulationResultEntity(
            uuid,
            result.getMaxTakeOffQueue(),
            result.getAvgTakeOffWait(),
            result.getMaxHoldQueue(),
            result.getAvgHoldTime(),
            result.getTotalCancellations(),
            result.getTotalDiversions(),
            result.getAvgArrivalDelay(),
            result.getAvgDepartureDelay()
        ));
        simRepo.findById(uuid).ifPresent(sim -> {
            sim.setStatus("COMPLETED");
            simRepo.save(sim);
        });
    }

    /** Append a single event log entry for a simulation. */
    public void saveEventLogEntry(String uuid, String eventType, int simTimestamp, String attributesJson) {
        logRepo.save(new EventLogEntryEntity(uuid, eventType, simTimestamp, attributesJson));
    }

    public long getEventLogCount(String uuid) {
        return logRepo.countBySimulationId(uuid);
    }

    /** Get the status of a simulation (RUNNING / COMPLETED / not found). */
    public Optional<String> getStatus(String uuid) {
        return simRepo.findById(uuid).map(SimulationEntity::getStatus);
    }

    /** Get paginated event log entries for a simulation. offset/count match the API design. */
    public List<EventLogEntryEntity> getEventLog(String uuid, int offset, int count) {
        return logRepo.findBySimulationIdOrderBySimTimestampAsc(
            uuid, PageRequest.of(offset / count, count)
        );
    }

    /** Get the final result metrics for a completed simulation. */
    public Optional<SimulationResultEntity> getResult(String uuid) {
        return resultRepo.findById(uuid);
    }

    /** Get the initial configuration JSON that was submitted for this simulation. */
    public Optional<String> getInitialConfiguration(String uuid) {
        return simRepo.findById(uuid).map(SimulationEntity::getConfig);
    }
}