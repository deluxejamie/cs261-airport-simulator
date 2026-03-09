package uk.ac.warwick.dcs.airportsimulator.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.events.IEvent;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;
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
    public void testRunways() throws IOException {
        final List<Runway> runways = simulation.getRunways();

        assertEquals(4, runways.size());


        final RunwayMode[] expected = {RunwayMode.MIXED_MODE, RunwayMode.TAKE_OFF, RunwayMode.LANDING, RunwayMode.MIXED_MODE};
        for (int i = 0; i < 4; ++i) {
            assertEquals(runways.get(i).getRunwayNumber(), i + 1);
            assertEquals(runways.get(i).getMode(), expected[i]);
        }
    }

    @Test
    public void testAdvancedConfig() {
        assertEquals(parsedConfig.getMaxDelayBeforeCancelled(), simulation.getMaxDelayBeforeCancelled());
        assertEquals(parsedConfig.getFuelThresholdBeforeRedirected(), simulation.getFuelThresholdBeforeRedirected());
        assertEquals(parsedConfig.getTimeTakenForTakeoff(), simulation.getTimeTakenForTakeoff());
        assertEquals(parsedConfig.getTimeTakenForLanding(), simulation.getTimeTakenForLanding());
    }

    @Test
    public void testFlights()
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
            /* assertEquals(1, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.DEPARTURE, flight.getOp());
        }

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.get(1);
            assertEquals("RYANAIR-2", flight.getAircraft().getCallSign());
            assertEquals(50, flight.getScheduled());
            assertEquals(90712, flight.getSeed());
            assertEquals(1000, flight.getEnd());
            assertEquals(10, flight.getInterval());
            /* assertEquals(2, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.DEPARTURE, flight.getOp());
        }

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.get(2);
            assertEquals("BA-3", flight.getAircraft().getCallSign());
            assertEquals(68, flight.getScheduled());
            assertEquals(10866, flight.getSeed());
            /* assertEquals(3, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.ARRIVAL, flight.getOp());
            assertEquals(EmergencyStatus.NONE, flight.getAircraft().getEmergencyStatus());
            assertEquals(20, flight.getAircraft().getFuelRemaining(68));
        }

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.get(3);
            assertEquals("NW-4", flight.getAircraft().getCallSign());
            assertEquals(65, flight.getScheduled());
            assertEquals(1735, flight.getSeed());
            /* assertEquals(4, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.ARRIVAL, flight.getOp());
            assertEquals(EmergencyStatus.MECH_FAIL, flight.getAircraft().getEmergencyStatus());
            assertEquals(16, flight.getAircraft().getFuelRemaining(65));
        }

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.get(4);
            assertEquals("EVA AIR-5", flight.getAircraft().getCallSign());
            assertEquals(879, flight.getScheduled());
            assertEquals(54053, flight.getSeed());
            /* assertEquals(5, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.ARRIVAL, flight.getOp());
            assertEquals(EmergencyStatus.PASSENGER_HEALTH, flight.getAircraft().getEmergencyStatus());
            assertEquals(78, flight.getAircraft().getFuelRemaining(879));
        }

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.get(5);
            assertEquals("CATHY-6", flight.getAircraft().getCallSign());
            assertEquals(6575, flight.getScheduled());
            assertEquals(89027, flight.getSeed());
            assertEquals(13421, flight.getEnd());
            assertEquals(23, flight.getInterval());
            /* assertEquals(6, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.ARRIVAL, flight.getOp());
            assertEquals(EmergencyStatus.NONE, flight.getAircraft().getEmergencyStatus());
            assertEquals(65, flight.getAircraft().getFuelRemaining(6575));
        }

        {
            final ParsedSimulationConfig.ParsedFlight flight = parsedFlights.get(6);
            assertEquals("VIRGIN-7", flight.getAircraft().getCallSign());
            assertEquals(23, flight.getScheduled());
            assertEquals(18875, flight.getSeed());
            assertEquals(6757, flight.getEnd());
            assertEquals(65, flight.getInterval());
            /* assertEquals(7, flight.getAircraft().getId()); */
            assertEquals(AircraftOp.ARRIVAL, flight.getOp());
            assertEquals(EmergencyStatus.PASSENGER_HEALTH, flight.getAircraft().getEmergencyStatus());
            assertEquals(54, flight.getAircraft().getFuelRemaining(23));
        }
    }

    @Test
    public void TestRunwayClosures()
    {
        final List<ParsedSimulationConfig.ParsedRunwayClosure> parsedRunwayClosures = parsedSimulationConfig.getRunwayClosures();

        assertEquals(3, parsedRunwayClosures.size());

        {
            final ParsedSimulationConfig.ParsedRunwayClosure closure = parsedRunwayClosures.getFirst();
            assertEquals(1, closure.getRunwayNumber());
            assertEquals(34, closure.getScheduled());
            assertEquals(0, closure.getInterval());
            assertEquals(34 + 15, closure.getEnd());
            assertEquals(RunwayStatus.SNOW_CLEARANCE, closure.getStatus());
        }


        {
            final ParsedSimulationConfig.ParsedRunwayClosure closure = parsedRunwayClosures.get(1);
            assertEquals(3, closure.getRunwayNumber());
            assertEquals(56, closure.getScheduled());
            assertEquals(0, closure.getInterval());
            assertEquals(34 + 56, closure.getEnd());
            assertEquals(RunwayStatus.RUNWAY_INSPECTION, closure.getStatus());
        }

        {
            final ParsedSimulationConfig.ParsedRunwayClosure closure = parsedRunwayClosures.get(2);
            assertEquals(4, closure.getRunwayNumber());
            assertEquals(34, closure.getScheduled());
            assertEquals(0, closure.getInterval());
            assertEquals(34 + 54, closure.getEnd());
            assertEquals(RunwayStatus.EQUIPMENT_FAILURE, closure.getStatus());
        }
    }

    @Test
    public void testEmergencyEvents()
    {
        final List<ParsedSimulationConfig.ParsedEmergencyEvent> emergencyEvents = parsedSimulationConfig.getEmergencyEvents();
        assertEquals(2, emergencyEvents.size());

        {
            final ParsedSimulationConfig.ParsedEmergencyEvent e = emergencyEvents.getFirst();
            assertEquals("EVA AIR-5", e.getCallsign());
            assertEquals(EmergencyStatus.PASSENGER_HEALTH ,e.getEmergencyStatus());
            assertEquals(879, e.getScheduled());
        }


        {
            final ParsedSimulationConfig.ParsedEmergencyEvent e = emergencyEvents.get(1);
            assertEquals("NW-4", e.getCallsign());
            assertEquals(EmergencyStatus.MECH_FAIL ,e.getEmergencyStatus());
            assertEquals(65, e.getScheduled());
        }
    }
}
