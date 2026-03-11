package uk.ac.warwick.dcs.airportsimulator.simulation;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    @Test
    void testConstructorInitialisesState() {
        final Simulation sim = genBaseSim();

        assertEquals(2, sim.getRunways().size());
        assertNotNull(sim.getHoldingPattern());
        assertNotNull(sim.getTakeOffQueue());
        assertNotNull(sim.getEventSchedular());
        assertNotNull(sim.getEventLogStore());
        assertEquals(0, sim.getEventLog(0, 100).size());
    }

    @Test
    void testIsFinishedTrueWhenEmpty() {
        final Simulation sim = genBaseSim();
        assertTrue(sim.isFinished());
    }

    @Test
    void testIsFinishedFalseWhenHoldingPatternNotEmpty() {
        final Simulation sim = genBaseSim();
        sim.getHoldingPattern().addAircraft(genAircraft());
        assertFalse(sim.isFinished());
    }

    @Test
    void testIsFinishedFalseWhenTakeOffQueueNotEmpty() {
        final Simulation sim = genBaseSim();
        sim.getTakeOffQueue().addAircraft(genAircraft());
        assertFalse(sim.isFinished());
    }

    @Test
    void testIsFinishedFalseWhenRunwayOccupied() {
        final Simulation sim = genBaseSim();
        final Runway runway = sim.getRunways().getFirst();
        runway.setOccupied(genAircraft());
        assertFalse(sim.isFinished());
    }

    @Test
    void testIsFinishedFalseWhenEventPending() {
        final Simulation sim = genBaseSim();
        sim.addRunwayStatusChange(0, 10, 0, 0, RunwayStatus.SNOW_CLEARANCE);
        assertFalse(sim.isFinished());
    }

    @Test
    void testGetEventLogInitiallyEmpty() {
        final Simulation sim = genBaseSim();
        assertEquals(0, sim.getEventLog(0, 10).size());
    }

    @Test
    void testGetEventLogAfterScheduledEventExecutes() {
        final Simulation sim = genBaseSim();
        final SimulationAddAircraftRunwayTest.SimTester simTester = new SimulationAddAircraftRunwayTest.SimTester(sim);
        sim.addRunwayStatusChange(0, 1, 0, 0, RunwayStatus.SNOW_CLEARANCE);
        simTester.step(5);

        assertEquals(1, sim.getEventLog(0, 10).size());
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
                100,
                30000,
                450,
                120,
                EmergencyStatus.NONE,
                50
        );
    }
}