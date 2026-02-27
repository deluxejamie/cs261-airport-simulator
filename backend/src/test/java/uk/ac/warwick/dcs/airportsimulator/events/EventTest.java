package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void isRecurring_shouldBeFalseWhenIntervalIsZero() {
        Event e = new Event(10, () -> {});
        assertFalse(e.isRecurring());
    }

    @Test
    void isRecurring_shouldBeTrueWhenIntervalGreaterThanZero() {
        Event e = new Event(10, 5, 50, () -> {});
        assertTrue(e.isRecurring());
    }

    @Test
    void execute_shouldRunAction() {
        AtomicInteger counter = new AtomicInteger(0);
        Event e = new Event(0, counter::incrementAndGet);

        e.execute();

        assertEquals(1, counter.get());
    }

    @Test
    void next_shouldReturnNullForNonRecurringEvent() {
        Event e = new Event(10, () -> {});
        assertNull(e.next());
    }

    @Test
    void next_shouldCreateNextEventUntilEndTime() {
        AtomicInteger counter = new AtomicInteger(0);
        Event e = new Event(10, 5, 25, counter::incrementAndGet);

        Event n1 = e.next();
        assertNotNull(n1);
        assertEquals(15.0, n1.getScheduledTime(), 1e-9);

        Event n2 = n1.next();
        assertNotNull(n2);
        assertEquals(20.0, n2.getScheduledTime(), 1e-9);

        Event n3 = n2.next();
        assertNotNull(n3);
        assertEquals(25.0, n3.getScheduledTime(), 1e-9);

        Event n4 = n3.next();
        assertNull(n4);
    }
}


