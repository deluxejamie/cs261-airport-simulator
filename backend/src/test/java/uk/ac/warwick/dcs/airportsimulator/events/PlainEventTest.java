package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PlainEventTest {

    @Test
    void oneShotEvent_shouldNotBeRecurring_andNextShouldBeNull() {
        PlainEvent e = new PlainEvent(10, () -> {});
        assertFalse(e.isRecurring());
        assertEquals(10, e.getScheduledTime());
        assertNull(e.next());
    }

    @Test
    void execute_shouldRunAction() {
        AtomicInteger counter = new AtomicInteger(0);
        PlainEvent e = new PlainEvent(0, counter::incrementAndGet);

        e.execute();

        assertEquals(1, counter.get());
    }

    @Test
    void recurringEvent_nextShouldAdvanceByInterval_untilEndTimeExclusive() {
        AtomicInteger counter = new AtomicInteger(0);
        PlainEvent e = new PlainEvent(10, 5, 25, counter::incrementAndGet);

        assertTrue(e.isRecurring());

        IEvent n1 = e.next(); // 15
        assertNotNull(n1);
        assertEquals(15, n1.getScheduledTime());

        IEvent n2 = n1.next(); // 20
        assertNotNull(n2);
        assertEquals(20, n2.getScheduledTime());

        IEvent n3 = n2.next(); // 25 => should be null because nextTime >= endTime
        assertNull(n3);
    }
}