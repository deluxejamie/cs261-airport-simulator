package uk.ac.warwick.dcs.airportsimulator.eventlog;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EventLogEntryTest {

    @Test
    void constructor_shouldSetAllAttributesCorrectly() {
        // Arrange
        EventType type = EventType.LANDING_EVENT;
        double timestamp = 123.45;

        HashMap<String, Object> attr = new HashMap<>();
        attr.put("runway", 1);
        attr.put("message", "Landing scheduled");

        // Act (constructor is package-private, so test must be in same package)
        EventLogEntry entry = new EventLogEntry(type, timestamp, attr);

        // Assert
        assertAll(
                () -> assertEquals(type, entry.getType()),
                () -> assertEquals(timestamp, entry.getTimestamp(), 1e-9),
                () -> assertSame(attr, entry.getAttr())
        );
    }

    @Test
    void attrMap_shouldBeSameReference_soMutationsAreVisible() {
        EventLogEntry entry = new EventLogEntry(
                EventType.EMERGENCY_EVENT,
                1.0,
                new HashMap<>()
        );

        entry.getAttr().put("severity", "HIGH");

        assertEquals("HIGH", entry.getAttr().get("severity"));
    }
}
