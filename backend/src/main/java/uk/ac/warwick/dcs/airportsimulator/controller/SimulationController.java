package uk.ac.warwick.dcs.airportsimulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.Tuple;
import tools.jackson.databind.ObjectMapper;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationResultEntity;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationService;

import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.service.SimManager;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationControlService;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller exposing GET endpoints for retrieving simulation status, event logs, and final simulation results.
 */
@RestController
@RequestMapping("/simulation")
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
    public Map<String, String> requestSimulation(@RequestBody SimulationRequestDto requestDto) {
        final Simulation simulation = simulationControlService.buildSimulationFromRequest(requestDto);
        final String uuid = java.util.UUID.randomUUID().toString();
        simManager.runSimulation(simulation, uuid, requestDto.toString());

        return Map.of("sim_id", uuid);
    }


    /**
     * Gets the current status of a simulation
     *
     * @param uuid unique identifier of simulation
     * @return HTTP 200 with the simulation status if found,otherwise HTTP 404
     */
    @GetMapping("/status/{uuid}")
    public Map<String, String> getStatus(@PathVariable String uuid) {
        final Optional<String> fromManager = simManager.getStatus(uuid);
        if (fromManager.isPresent()) {
            return Map.of("status", fromManager.get());
        }

        final Optional<String> fromDb = simulationService.getStatus(uuid);
        return fromDb.map(s -> Map.of("status", s)).orElseGet(() -> Map.of("status", "unavailable"));
    }

    /**
     * Gets the event log for a simulation.
     *
     * @param uuid   unique identifier of simulation
     * @param offset starting index in event log
     * @param count  number of events to return
     * @return HTTP 200 with event log data if the request is valid,otherwise HTTP 400
     */
    @GetMapping("/eventlog/{uuid}/{offset}/{count}")
    public ResponseEntity<?> getEventLog(@PathVariable String uuid, @PathVariable int offset, @PathVariable int count) {
        if (offset < 0 || count <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "offset must be more than or equal to 0 and count>0"));
        }


        final Optional<Tuple<List<EventLogEntry>, Long>> fromMem = simManager.getEventLog(uuid, offset, count);
        if (fromMem.isPresent()) {
            final List<EventLogEntry> eventLogEntries = fromMem.get()._1();
            final List<EventLogItemResponse> eventItems = new ArrayList<>(eventLogEntries.size());

            final ObjectMapper objectMapper = new ObjectMapper();
            for (int i = 0; i < eventLogEntries.size(); ++i) {
                final EventLogEntry e = eventLogEntries.get(i);
                eventItems.add(new EventLogItemResponse(
                                offset + i + 1,
                                e.getType().toString().toLowerCase(),
                                e.getTimestamp(),
                                objectMapper.writeValueAsString(e.getAttr())
                        )
                );
            }

            return ResponseEntity.ok(new EventLogResponse(
                    eventItems, fromMem.get()._2()
            ));
        }


        final List<EventLogEntryEntity> fromDb = simulationService.getEventLog(uuid, offset, count);
        final List<EventLogItemResponse> eventItems = new ArrayList<>(fromDb.size());

        for (int i = 0; i < fromDb.size(); ++i) {
            final EventLogEntryEntity e = fromDb.get(i);
            eventItems.add(new EventLogItemResponse(
                            offset + i + 1,
                            e.getEventType(),
                            e.getSimTimestamp(),
                            e.getAttributes()
                    )
            );
        }

        final long totalEvents = simulationService.getEventLogCount(uuid);
        return ResponseEntity.ok(new EventLogResponse(eventItems, totalEvents));
    }

    /**
     * Gets the final result metrics for a simulation.
     *
     * @param uuid unique identifier of simulation
     * @return HTTP 200 with the simulation result if found,otherwise HTTP 404
     */
    @GetMapping("/result/{uuid}")
    public ResponseEntity<?> getResult(@PathVariable String uuid) {
        final Optional<SimulationResultEntity> result = simulationService.getResult(uuid);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }
}

