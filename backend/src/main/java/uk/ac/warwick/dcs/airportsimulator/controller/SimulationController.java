package uk.ac.warwick.dcs.airportsimulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationResultEntity;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationService;

import org.springframework.web.bind.annotation.*;
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
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimManager simManager;
    private final SimulationService simulationService;
    private final SimulationControlService simulationControlService;


    /**
     * Constructs a SimulationController with the required service
     * @param simulationService service used to retrieve simulation data
     */
    public SimulationController(SimulationService simulationService, SimulationControlService simulationControlService, SimManager simManager) {
        this.simManager = simManager;
        this.simulationService = simulationService;
        this.simulationControlService = simulationControlService;
    }

    /**
     * Request simulation
     * @param requestDto request config
     * @return           the uuid for the simulation
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestSimulation(@RequestBody SimulationRequestDto requestDto) {
        final Simulation simulation = simulationControlService.buildSimulationFromRequest(requestDto);
        final String simId = java.util.UUID.randomUUID().toString(); /* If it is not necessary to return the sim_id, delete this line */
        simManager.RunSimulation(simulation, simId);

        return ResponseEntity.ok(simId);
    }


    /**
     * Gets the current status of a simulation
     * @param uuid unique identifier of simulation
     * @return HTTP 200 with the simulation status if found,otherwise HTTP 404
     */
    @GetMapping("/{uuid}/status")
    public ResponseEntity<?> getStatus(@PathVariable String uuid) {
        Optional<String> status = simulationService.getStatus(uuid);
        if (status.isPresent()) {
            return ResponseEntity.ok(Map.of("status", status.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Gets the event log for a simulation.
     * @param uuid unique identifier of simulation
     * @param offset starting index in event log
     * @param count number of events to return
     * @return HTTP 200 with event log data if the request is valid,otherwise HTTP 400
     */
    @GetMapping("/{uuid}/event-log")
    public ResponseEntity<?> getEventLog(@PathVariable String uuid,@RequestParam(defaultValue = "0") int offset,@RequestParam(defaultValue = "50") int count) {
        if (offset<0 || count<=0) {
            return ResponseEntity.badRequest().body(Map.of("error", "offset must be more than or equal to 0 and count>0"));
        }
        List<EventLogEntryEntity> logs = simulationService.getEventLog(uuid, offset, count);
        List<EventLogItemResponse> eventItems = new ArrayList<>();

        for (EventLogEntryEntity log : logs) {
            eventItems.add(new EventLogItemResponse(
                    log.getEventType(),
                    log.getSimTimestamp(),
                    log.getAttributes()
            ));
        }

        long totalEvents = simulationService.getEventLogCount(uuid);
        EventLogResponse response = new EventLogResponse(eventItems, totalEvents);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the final result metrics for a simulation.
     * @param uuid unique identifier of simulation
     * @return HTTP 200 with the simulation result if found,otherwise HTTP 404
     */
    @GetMapping("/{uuid}/result")
    public ResponseEntity<?> getResult(@PathVariable String uuid) {
        Optional<SimulationResultEntity> result = simulationService.getResult(uuid);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }
}

