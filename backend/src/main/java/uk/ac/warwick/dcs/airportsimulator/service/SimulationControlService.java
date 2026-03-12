package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

/**
 * Service responsible for coordinating the conversion of a frontend simulation
 * request into a fully constructed {@link Simulation}.
 *
 * This service delegates:
 *      request parsing and validation to {@link SimulationConfigParser}，
 *      simulation object construction to {@link SimulationSetupService}.
 *
 */
@Service
public class SimulationControlService {

    private final SimulationConfigParser simulationConfigParser;
    private final SimulationSetupService simulationSetupService;

    /**
     * Creates a control service with its required collaborators.
     *
     * @param simulationConfigParser parser used to validate and parse request DTOs
     * @param simulationSetupService service used to build simulation instances
     */
    public SimulationControlService(SimulationConfigParser simulationConfigParser,
                                    SimulationSetupService simulationSetupService) {
        this.simulationConfigParser = simulationConfigParser;
        this.simulationSetupService = simulationSetupService;
    }

    /**
     * Builds a simulation instance from a frontend request.
     *
     * @param request simulation request DTO
     * @return fully initialised simulation
     */
    public Simulation buildSimulationFromRequest(SimulationRequestDto request) {
        ParsedSimulationConfig parsedConfig = simulationConfigParser.parseSimulationRequest(request);
        return simulationSetupService.setupSimulation(parsedConfig);
    }
}