package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventSchedularTest {

    @Test
    void step_shouldExecuteEventsInTimeOrder() {
        EventSchedular s = new EventSchedular();

        List<String> order = new ArrayList<>();

        s.addEvent(new PlainEvent(10, () -> order.add("A")));
        s.addEvent(new PlainEvent(5, () -> order.add("B")));

        s.step(4);
        assertTrue(order.isEmpty());

        s.step(5);
        assertEquals(List.of("B"), order);

        s.step(10);
        assertEquals(List.of("B", "A"), order);
    }

    @Test
    void step_shouldRescheduleRecurringEventsUntilEndTimeExclusive() {
        EventSchedular s = new EventSchedular();

        AtomicInteger counter = new AtomicInteger(0);

        // PlainEvent.next stops when nextTime >= endTime
        // With endTime=16, occurrences at 0,5,10,15 (next would be 20 >= 16 => null)
        s.addEvent(new PlainEvent(0, 5, 16, counter::incrementAndGet));

        s.step(100); // large simTime should process all due events in one go
        assertEquals(4, counter.get());
    }
}