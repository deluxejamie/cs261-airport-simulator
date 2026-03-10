package uk.ac.warwick.dcs.airportsimulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.Tuple;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationResultEntity;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationService;

import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.service.SimManager;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationControlService;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller exposing GET endpoints for retrieving simulation status, event logs, and final simulation results.
 */
@RestController
@RequestMapping("/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimManager simManager;
    private final SimulationService simulationService;
    private final SimulationControlService simulationControlService;


    /**
     * Constructs a SimulationController with the required service
     *
     * @param simulationService service used to retrieve simulation data
     */
    public SimulationController(SimulationService simulationService, SimulationControlService simulationControlService, SimManager simManager) {
        this.simManager = simManager;
        this.simulationService = simulationService;
        this.simulationControlService = simulationControlService;
    }

    /**
     * Request simulation
     *
     * @param requestDto request config
     * @return the uuid for the simulation
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestSimulation(@RequestBody SimulationRequestDto requestDto) {
        final Simulation simulation = simulationControlService.buildSimulationFromRequest(requestDto);
        final String simId = java.util.UUID.randomUUID().toString(); /* If it is not necessary to return the sim_id, delete this line */
        simManager.runSimulation(simulation, simId);

        return ResponseEntity.ok(simId);
    }


    /**
     * Gets the current status of a simulation
     *
     * @param uuid unique identifier of simulation
     * @return HTTP 200 with the simulation status if found,otherwise HTTP 404
     */
    @GetMapping("/{uuid}/status")
    public ResponseEntity<?> getStatus(@PathVariable String uuid) {
        final Optional<String> fromManager = simManager.getStatus(uuid);
        if (fromManager.isPresent()) {
            return ResponseEntity.ok(Map.of("status", fromManager.get()));
        }

        final Optional<String> fromDb = simulationService.getStatus(uuid);
        if (fromDb.isPresent()) {
            return ResponseEntity.ok(Map.of("status", fromDb.get()));
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Gets the event log for a simulation.
     *
     * @param uuid   unique identifier of simulation
     * @param offset starting index in event log
     * @param count  number of events to return
     * @return HTTP 200 with event log data if the request is valid,otherwise HTTP 400
     */
    @GetMapping("/{uuid}/event-log")
    public ResponseEntity<?> getEventLog(@PathVariable String uuid, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "50") int count) {
        if (offset < 0 || count <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "offset must be more than or equal to 0 and count>0"));
        }


        final Optional<Tuple<List<EventLogEntry>, Long>> fromMem = simManager.getEventLog(uuid, offset, count);
        if (fromMem.isPresent()) {
            final List<EventLogItemResponse> eventItems = fromMem.get()
                    ._1()
                    .stream()
                    .map((og) -> new EventLogItemResponse(
                            og.getType().toString(),
                            og.getTimestamp(),
                            og.getAttr().toString()
                    ))
                    .toList();

            return ResponseEntity.ok(new EventLogResponse(
               eventItems, fromMem.get()._2()
            ));
        }


        final List<EventLogEntryEntity> fromDb = simulationService.getEventLog(uuid, offset, count);
        final List<EventLogItemResponse> eventItems = fromDb
                .stream()
                .map((og) -> new EventLogItemResponse(
                        og.getEventType(),
                        og.getSimTimestamp(),
                        og.getAttributes()
                ))
                .toList();

        final long totalEvents = simulationService.getEventLogCount(uuid);
        return ResponseEntity.ok(new EventLogResponse(eventItems, totalEvents));
    }

    /**
     * Gets the final result metrics for a simulation.
     *
     * @param uuid unique identifier of simulation
     * @return HTTP 200 with the simulation result if found,otherwise HTTP 404
     */
    @GetMapping("/{uuid}/result")
    public ResponseEntity<?> getResult(@PathVariable String uuid) {
        final Optional<SimulationResultEntity> result = simulationService.getResult(uuid);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }
}

