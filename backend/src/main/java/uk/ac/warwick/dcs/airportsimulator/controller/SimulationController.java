package uk.ac.warwick.dcs.airportsimulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.service.SimManager;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationControlService;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

@RestController
@RequestMapping("/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimManager simManager;
    private final SimulationControlService simulationControlService;

    public SimulationController(SimManager simManager, SimulationControlService simulationControlService) {
        this.simManager = simManager;
        this.simulationControlService = simulationControlService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestSimulation(@RequestBody SimulationRequestDto requestDto) {
        final Simulation simulation = simulationControlService.buildSimulationFromRequest(requestDto);
        final String simId = java.util.UUID.randomUUID().toString(); /* If it is not necessary to return the sim_id, delete this line */
        simManager.RunSimulation(simulation, simId);

        return ResponseEntity.ok(simId);
    }
}