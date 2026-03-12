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
        final Simulation sim = genBaseSim();
        final SimulationResult result = sim.run();

        assertNotNull(result);
        assertTrue(sim.isFinished());
    }

    @Test
    void testRunProcessesArrivalEvent() {
        final Simulation sim = genBaseSim();

        sim.addAircraft(genAircraft(), 1, 0, 0, 0L, AircraftOp.ARRIVAL);
        sim.run();

        assertFalse(sim.getEventLog(0, 100).isEmpty());
    }

    @Test
    void testRunProcessesDepartureEvent() {
        final Simulation sim = genBaseSim();
        sim.addAircraft(genAircraft(), 1, 0, 0, 0L, AircraftOp.DEPARTURE);
        sim.run();
        assertFalse(sim.getEventLog(0, 100).isEmpty());
    }

    private Simulation genBaseSim() {
        final List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(0, 1000, 120, RunwayMode.TAKE_OFF, RunwayStatus.AVAILABLE, null));
        runways.add(new Runway(1, 1000, 120, RunwayMode.LANDING, RunwayStatus.AVAILABLE, null));
        return new Simulation(runways);
    }

    @Test
    void testRunResultForSingleArrival() {
        final Simulation sim = genBaseSim();

        final Aircraft arrival = genAircraft();
        sim.addAircraft(arrival, 1, 0, 0, 0L, AircraftOp.ARRIVAL);

        final SimulationResult result = sim.run();

        assertNotNull(result);
        assertTrue(result.getMaxHoldQueue() >= 1);
        assertEquals(0, result.getTotalDiversions());
        assertEquals(0, result.getTotalCancellations());
    }

    @Test
    void testRunResultForSingleDeparture() {
        final Simulation sim = genBaseSim();

        final Aircraft departure = genAircraft();
        sim.addAircraft(departure, 1, 0, 0, 0L, AircraftOp.DEPARTURE);

        final SimulationResult result = sim.run();

        assertNotNull(result);
        assertTrue(result.getMaxTakeOffQueue() >= 1);
        assertEquals(0, result.getTotalDiversions());
        assertEquals(0, result.getTotalCancellations());
    }

    @Test
    void testRunResultForMixedTraffic() {
        final Simulation sim = genBaseSim();

        final Aircraft arrival1 = genAircraft("ARR1", 1, 20);
        final Aircraft arrival2 = genAircraft("ARR2", 2, 25);
        final Aircraft departure1 = genAircraft("DEP1", 1, 120);
        final Aircraft departure2 = genAircraft("DEP2", 2, 120);

        sim.addAircraft(arrival1, 1, 0, 0, 0L, AircraftOp.ARRIVAL);
        sim.addAircraft(arrival2, 2, 0, 0, 0L, AircraftOp.ARRIVAL);
        sim.addAircraft(departure1, 1, 0, 0, 0l, AircraftOp.DEPARTURE);
        sim.addAircraft(departure2, 2, 0, 0, 0L, AircraftOp.DEPARTURE);

        SimulationResult result = sim.run();

        assertNotNull(result);
        assertTrue(result.getMaxHoldQueue() >= 1);
        assertTrue(result.getMaxTakeOffQueue() >= 1);
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

    private Aircraft genAircraft(String callSign, int scheduledTime, int initialFuel) {
        return new Aircraft(
                callSign,
                "LHR",
                "EDI",
                scheduledTime,
                30000,
                450,
                initialFuel,
                EmergencyStatus.NONE,
                0
        );
    }
}