package uk.ac.warwick.dcs.airportsimulator.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.events.IEvent;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.simulation.AircraftOp;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateSimTest extends BaseServiceTest {

    Simulation simulation;
    ParsedSimulationConfig parsedSimulationConfig;

    @BeforeEach
    public void setupSimulation() throws Exception {
        final SimulationConfigParser simulationConfigParser = new SimulationConfigParser();
        parsedSimulationConfig = simulationConfigParser.parseSimulationRequest(json.parseObject(JSON_FROM_FILE));

        final SimulationSetupService simulationSetupService = new SimulationSetupService();
        simulation = simulationSetupService.setupSimulation(parsedSimulationConfig);
    }

    @Test
    void testRunways() throws IOException {
        final List<Runway> runways = simulation.getRunways();

        assertEquals(4, runways.size());


        final RunwayMode[] expected = {RunwayMode.MIXED_MODE, RunwayMode.TAKE_OFF, RunwayMode.LANDING, RunwayMode.MIXED_MODE};
        for (int i = 0; i < 4; ++i) {
            assertEquals(runways.get(i).getRunwayNumber(), i + 1);
            assertEquals(runways.get(i).getMode(), expected[i]);
        }
    }

    @Test
    void testAdvancedConfig() throws Exception {
        throw new Exception("Advanced config not set on simulation!");
    }

    @Test
    void testFlights()
    {
        final List<ParsedSimulationConfig.ParsedFlight> parsedFlights = parsedSimulationConfig.getFlights();
        assertEquals(7, parsedFlights.size());

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.getFirst();
            assertEquals("EASYJET-1", flight.getAircraft().getCallSign());
            assertEquals(0, flight.getScheduled());
            assertEquals(24880, flight.getSeed());
            assertEquals(60, flight.getEnd());
            assertEquals(10, flight.getInterval());
            assertEquals(1, flight.getAircraft().getId());
            assertEquals(AircraftOp.DEPARTURE, flight.getOp());
        }
    }
}
