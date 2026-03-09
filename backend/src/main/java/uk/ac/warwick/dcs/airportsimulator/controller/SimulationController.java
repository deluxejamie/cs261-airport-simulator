package uk.ac.warwick.dcs.airportsimulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationResultEntity;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/simulations")

public class SimulationController {
    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/{uuid}/status")
    public ResponseEntity<?> getStatus(@PathVariable String uuid) {
        Optional<String> status = simulationService.getStatus(uuid);
        if (status.isPresent()) {
            return ResponseEntity.ok(Map.of("status", status.get()));
        }
        return ResponseEntity.notFound().build();
    }

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

    @GetMapping("/{uuid}/result")
    public ResponseEntity<?> getResult(@PathVariable String uuid) {
        Optional<SimulationResultEntity> result = simulationService.getResult(uuid);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }
}

