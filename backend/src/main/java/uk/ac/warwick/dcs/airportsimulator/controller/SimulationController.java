package uk.ac.warwick.dcs.airportsimulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationControlService;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

@RestController
@RequestMapping("/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationControlService simulationControlService;

    public SimulationController(SimulationControlService simulationControlService) {
        this.simulationControlService = simulationControlService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestSimulation(@RequestBody SimulationRequestDto requestDto) {
        Simulation simulation = simulationControlService.buildSimulationFromRequest(requestDto);
        String simId = java.util.UUID.randomUUID().toString(); /* If it is not necessary to return the sim_id, delete this line */
        simulation.run();

        return ResponseEntity.ok("Simulation created successfully.");
    }
}