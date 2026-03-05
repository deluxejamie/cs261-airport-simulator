package uk.ac.warwick.dcs.airportsimulator.priorityqueue;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;

import static org.junit.jupiter.api.Assertions.*;

public class HoldingPatternTest {

    private Aircraft createAircraft(String callSign, int fuel, EmergencyStatus status) {
        return new Aircraft(
                callSign,
                "AAA",
                "BBB",
                100,
                10000,
                500,
                fuel,
                status,
                0
        );
    }

    @Test
    void testAddAircraftIncreasesSize() {
        HoldingPattern hp = new HoldingPattern(0);

        Aircraft a = createAircraft("A1", 50, EmergencyStatus.NONE);

        hp.addAircraft(a);

        assertEquals(1, hp.size());
    }

    @Test
    void testEmergencyAircraftPriority() {
        HoldingPattern hp = new HoldingPattern(0);

        Aircraft normal = createAircraft("A1", 50, EmergencyStatus.NONE);
        Aircraft emergency = createAircraft("A2", 50, EmergencyStatus.FUEL);

        hp.addAircraft(normal);
        hp.addAircraft(emergency);

        assertEquals("A2", hp.peekNextAircraft().getCallSign());
    }

    @Test
    void testLowerFuelPriority() {
        HoldingPattern hp = new HoldingPattern(0);

        Aircraft highFuel = createAircraft("A1", 50, EmergencyStatus.NONE);
        Aircraft lowFuel = createAircraft("A2", 20, EmergencyStatus.NONE);

        hp.addAircraft(highFuel);
        hp.addAircraft(lowFuel);

        assertEquals("A2", hp.peekNextAircraft().getCallSign());
    }

    @Test
    void testFIFOWhenEqualPriority() {
        HoldingPattern hp = new HoldingPattern(0);

        Aircraft a1 = createAircraft("A1", 30, EmergencyStatus.NONE);
        Aircraft a2 = createAircraft("A2", 30, EmergencyStatus.NONE);

        hp.addAircraft(a1);
        hp.addAircraft(a2);

        assertEquals("A1", hp.getNextAircraft().getCallSign());
        assertEquals("A2", hp.getNextAircraft().getCallSign());
    }

    @Test
    void testCapacityLimit() {
        HoldingPattern hp = new HoldingPattern(1);

        Aircraft a1 = createAircraft("A1", 30, EmergencyStatus.NONE);
        Aircraft a2 = createAircraft("A2", 40, EmergencyStatus.NONE);

        assertTrue(hp.addAircraft(a1));
        assertFalse(hp.addAircraft(a2));
    }
}