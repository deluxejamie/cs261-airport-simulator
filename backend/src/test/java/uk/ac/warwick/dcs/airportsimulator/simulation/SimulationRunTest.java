package uk.ac.warwick.dcs.airportsimulator.simulation;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationRunTest {

    @Test
    void testRunReturnsResultForEmptySimulation() {
        Simulation sim = genBaseSim();
        SimulationResult result = sim.run();

        assertNotNull(result);
        assertTrue(sim.isFinished());
    }

    @Test
    void testRunProcessesArrivalEvent() {
        Simulation sim = genBaseSim();

        sim.addAircraft(genAircraft(), 1, 0, 0, AircraftOp.ARRIVAL);
        sim.run();

        assertFalse(sim.getEventLog(0, 100).isEmpty());
    }

    @Test
    void testRunProcessesDepartureEvent() {
        Simulation sim = genBaseSim();

        sim.addAircraft(genAircraft(), 1, 0, 0, AircraftOp.DEPARTURE);
        sim.run();

        assertFalse(sim.getEventLog(0, 100).isEmpty());
    }

    private Simulation genBaseSim() {
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(0, 1000, 120, RunwayMode.TAKE_OFF, RunwayStatus.AVAILABLE, null));
        runways.add(new Runway(1, 1000, 120, RunwayMode.LANDING, RunwayStatus.AVAILABLE, null));
        return new Simulation(runways);
    }

    private Aircraft genAircraft() {
        return new Aircraft(
                "BAW123",
                "LHR",
                "EDI",
                1,
                30000,
                450,
                120,
                EmergencyStatus.NONE,
                0
        );
    }
}