package uk.ac.warwick.dcs.airportsimulator.simulation;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventType;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayMode;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimulationAddAircraftRunwayTest {

    /**
     * Tests adding a non-recurring aircraft
     */
    @Test
    public void testAddAircraftPlain()
    {
        final Simulation sim = genBaseSim();

        sim.addAircraft(genAircraft(), 1, 0, 0, AircraftOp.ARRIVAL);
        sim.stepTestTillScrumMerged(0);

        assertEquals(0, sim.getEventLog(0, 100).size());

        sim.stepTestTillScrumMerged(100);
        assertEquals(1, sim.getEventLog(0, 100).size());

        {
            final EventLogEntry ele = sim.getEventLog(0, 100).getFirst();
            assertEquals(EventType.HOLDING_EVENT, ele.getType());
        }

        sim.addAircraft(genAircraft(), 150, 0, 0, AircraftOp.DEPARTURE);
        sim.stepTestTillScrumMerged(200);

        {
            final EventLogEntry ele = sim.getEventLog(0, 100).getLast();
            assertEquals(EventType.TAKEOFF_EVENT, ele.getType());
        }
    }


    /**
     * Tests adding a non-recurring operation change
     */
    @Test
    public void testRunwayOperationChange()
    {
        final Simulation sim = genBaseSim();

        sim.addRunwayOperationChange(0, 1, 0, 0, RunwayMode.LANDING);
        sim.stepTestTillScrumMerged(0);

        assertEquals(0, sim.getEventLog(0, 100).size());

        sim.stepTestTillScrumMerged(100);
        assertEquals(1, sim.getEventLog(0, 100).size());

        {
            final EventLogEntry ele = sim.getEventLog(0, 100).getFirst();
            assertEquals(EventType.RUNWAY_MODE_EVENT, ele.getType());
            assertEquals(RunwayMode.LANDING.toString(), ele.getAttr().get("mode"));
        }

        sim.addRunwayOperationChange(1, 150, 0, 0, RunwayMode.TAKE_OFF);
        sim.stepTestTillScrumMerged(200);

        {
            final EventLogEntry ele = sim.getEventLog(0, 100).getLast();
            assertEquals(EventType.RUNWAY_MODE_EVENT, ele.getType());
            assertEquals(RunwayMode.TAKE_OFF.toString(), ele.getAttr().get("mode"));
        }
    }


    /**
     * Tests adding a non-recurring aircraft emergency
     */
    @Test
    public void testAddAircraftEmergency()
    {
        final Simulation sim = genBaseSim();

        {
            final Aircraft a = genAircraft();
            sim.addAircraft(a, 50, 0, 0, AircraftOp.ARRIVAL);
            sim.addAircraftEmergency(a, 2, 0, 0, EmergencyStatus.MECH_FAIL);
            sim.addAircraftEmergency(a, 200, 0, 0, EmergencyStatus.FUEL);
        }


        sim.stepTestTillScrumMerged(0);
        assertEquals(0, sim.getEventLog(0, 100).size());

        sim.stepTestTillScrumMerged(2);
        assertEquals(1, sim.getEventLog(0, 100).size());

        {
            final EventLogEntry ele = sim.getEventLog(0, 100).getLast();
            assertEquals(EventType.EMERGENCY_EVENT, ele.getType());
        }

        sim.stepTestTillScrumMerged(98);

        {
            final EventLogEntry ele = sim.getEventLog(0, 100).getLast();
            assertEquals(EventType.HOLDING_EVENT, ele.getType());
        }

        sim.stepTestTillScrumMerged(200); // 300 atp

        {
            /* Can't have an emergency after the plane has landed */
            assertEquals(2, sim.getEventLog(0, 100).size());
        }

        {
            final Aircraft a = genAircraft();
            sim.addAircraft(a, 400, 0, 0, AircraftOp.DEPARTURE);
            sim.addAircraftEmergency(a, 500, 0, 0, EmergencyStatus.PASSENGER_HEALTH);
        }

        sim.stepTestTillScrumMerged(100); // 400 atp


        sim.stepTestTillScrumMerged(200); // 600 atp

        {
            /* Can't have an emergency on takeoff flights 
	     * (This can be ignored as should be dealt with elsewhere)
	     *
	     * final EventLogEntry ele = sim.getEventLog(0, 100).getLast();
	     * assertEquals(EventType.TAKEOFF_EVENT, ele.getType());
	     * */
                   }
    }


    /**
     * Tests adding a non-recurring runway status change
     */
    @Test
    public void testRunwayStatusChange()
    {
        final Simulation sim = genBaseSim();

        sim.addRunwayStatusChange(0, 20, 0, 0, RunwayStatus.SNOW_CLEARANCE);
        assertEquals(0, sim.getEventLog(0, 100).size());

        sim.stepTestTillScrumMerged(100);
        assertEquals(1, sim.getEventLog(0, 100).size());
        assertEquals(EventType.RUNWAY_STATUS_EVENT, sim.getEventLog(0, 100).getFirst().getType());

        assertEquals(RunwayStatus.SNOW_CLEARANCE.toString(), sim.getEventLog(0, 100).getFirst().getAttr().get("status"));
    }

    /**
     * Tests a recurring change
     * Note: as the behavior for adding recurring vs non-recurring is the
     *       same we for all tested functions we will only test one of them
     */
    @Test
    public void testRecurringAddition()
    {
        final Simulation sim = genBaseSim();
        final int start = 200;
        final int interval = 400;
        final int end = 1800;

        sim.addAircraft(genAircraft(), start, interval, end, AircraftOp.ARRIVAL);

        for (int i = 0; i <= end; i += interval)
        {
            assertEquals(i / interval, sim.getEventLog(0, 200).size());
            sim.stepTestTillScrumMerged(interval);
        }

        final int expected = 4;
        assertEquals(expected, sim.getEventLog(0, 200).size());
    }

    private Simulation genBaseSim()
    {
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(0, 1000, 120, RunwayMode.TAKE_OFF, RunwayStatus.AVAILABLE, null));
        runways.add(new Runway(1, 1000, 120, RunwayMode.LANDING, RunwayStatus.AVAILABLE, null));
        return new Simulation(runways);
    }

    private Aircraft genAircraft()
    {
        String callSign = "BAW123";
        String operator = "British Airways";
        String origin = "LHR";
        String destination = "EDI";
        int scheduledTime = 100;
        int altitude = 30000;
        int groundSpeed = 450;
        int initialFuel = 120;
        EmergencyStatus emergencyStatus = EmergencyStatus.NONE;
        int timeAddedToSim = 50;

        return new Aircraft(
                callSign, origin, destination,
                scheduledTime, altitude, groundSpeed,
                initialFuel, emergencyStatus, timeAddedToSim
        );
    }
}
