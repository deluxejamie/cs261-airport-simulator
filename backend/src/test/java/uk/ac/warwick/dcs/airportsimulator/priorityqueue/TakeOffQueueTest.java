package uk.ac.warwick.dcs.airportsimulator.priorityqueue;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;

import static org.junit.jupiter.api.Assertions.*;

public class TakeOffQueueTest {

    private Aircraft createAircraft(String callSign) {
        return new Aircraft(
                callSign,
                "AAA",
                "BBB",
                100,
                10000,
                500,
                50,
                EmergencyStatus.NONE,
                0
        );
    }

    @Test
    void testAddAircraftIncreasesSize() {
        TakeOffQueue queue = new TakeOffQueue();

        queue.addAircraft(createAircraft("A1"));

        assertEquals(1, queue.size());
    }

    @Test
    void testFIFOOrder() {
        TakeOffQueue queue = new TakeOffQueue();

        queue.addAircraft(createAircraft("A1"));
        queue.addAircraft(createAircraft("A2"));

        assertEquals("A1", queue.getNextAircraft().getCallSign());
        assertEquals("A2", queue.getNextAircraft().getCallSign());
    }

    @Test
    void testPeekDoesNotRemove() {
        TakeOffQueue queue = new TakeOffQueue();

        queue.addAircraft(createAircraft("A1"));

        assertEquals("A1", queue.peekNextAircraft().getCallSign());
        assertEquals(1, queue.size());
    }
}