package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

@Service
public class SimulationControlService {

    private final SimulationConfigParser simulationConfigParser;
    private final SimulationSetupService simulationSetupService;

    public SimulationControlService(SimulationConfigParser simulationConfigParser,
                                    SimulationSetupService simulationSetupService) {
        this.simulationConfigParser = simulationConfigParser;
        this.simulationSetupService = simulationSetupService;
    }

    public Simulation buildSimulationFromJson(String json) {
        ParsedSimulationConfig parsedConfig = simulationConfigParser.parseSimulationRequestJson(json);
        return simulationSetupService.setupSimulation(parsedConfig);
    }

    public Simulation buildSimulationFromRequest(SimulationRequestDto request) {
        ParsedSimulationConfig parsedConfig = simulationConfigParser.parseSimulationRequest(request);
        return simulationSetupService.setupSimulation(parsedConfig);
    }
}