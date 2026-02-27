package uk.ac.warwick.dcs.airportsimulator.eventlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {

    @Test
    void eventType_shouldContainExpectedConstants() {
        assertNotNull(EventType.valueOf("LANDING_EVENT"));
        assertNotNull(EventType.valueOf("TAKEOFF_EVENT"));
        assertNotNull(EventType.valueOf("HOLDING_EVENT"));
        assertNotNull(EventType.valueOf("DIVERSION_EVENT"));
        assertNotNull(EventType.valueOf("EMERGENCY_EVENT"));
        assertNotNull(EventType.valueOf("RUNWAY_ZONE_EVENT"));
        assertNotNull(EventType.valueOf("RUNWAY_MODE_EVENT"));
        assertNotNull(EventType.valueOf("CANCELLATION_EVENT"));
        assertNotNull(EventType.valueOf("RUNWAY_STATUS_EVENT"));
    }

    @Test
    void eventType_valuesOrder_shouldMatchDeclaration() {
        assertArrayEquals(
                new EventType[]{
                        EventType.LANDING_EVENT,
                        EventType.TAKEOFF_EVENT,
                        EventType.HOLDING_EVENT,
                        EventType.DIVERSION_EVENT,
                        EventType.EMERGENCY_EVENT,
                        EventType.RUNWAY_ZONE_EVENT,
                        EventType.RUNWAY_MODE_EVENT,
                        EventType.CANCELLATION_EVENT,
                        EventType.RUNWAY_STATUS_EVENT
                },
                EventType.values()
        );
    }
}
