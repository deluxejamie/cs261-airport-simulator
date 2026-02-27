package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventSchedularTest {

    @Test
    void addEvent_and_step_shouldExecuteEventsInTimeOrder() {
        EventSchedular s = new EventSchedular();

        AtomicInteger counter = new AtomicInteger(0);

        s.addEvent(new Event(10, counter::incrementAndGet));

        s.addEvent(new Event(5, counter::incrementAndGet));

        s.step(4);
        assertEquals(0, counter.get());

        s.step(5);
        assertEquals(1, counter.get());

        s.step(10);
        assertEquals(2, counter.get());
    }

    @Test
    void step_shouldRescheduleRecurringEventsUsingNext_untilEndTime() {
        EventSchedular s = new EventSchedular();

        AtomicInteger counter = new AtomicInteger(0);

        s.addEvent(new Event(0, 5, 15, counter::incrementAndGet));

        s.step(0);
        assertEquals(1, counter.get());

        s.step(5);
        assertEquals(2, counter.get());

        s.step(15);
        assertEquals(4, counter.get());

        s.step(20);
        assertEquals(4, counter.get());
    }
}
