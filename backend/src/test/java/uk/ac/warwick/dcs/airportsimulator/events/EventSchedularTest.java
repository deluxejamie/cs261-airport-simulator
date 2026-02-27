package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventSchedularTest {

    @Test
    void addEvent_and_step_shouldExecuteEventsInTimeOrder() {
        EventSchedular s = new EventSchedular();

        AtomicInteger counter = new AtomicInteger(0);

        s.addEvent(new PlainEvent(10, counter::incrementAndGet));

        s.addEvent(new PlainEvent(5, counter::incrementAndGet));

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

        s.addEvent(new PlainEvent(0, 5, 15, counter::incrementAndGet));

        s.step(0);
        assertEquals(1, counter.get());

        s.step(5);
        assertEquals(2, counter.get());

        s.step(15);
        assertEquals(3, counter.get());

        s.step(20);
        assertEquals(3, counter.get());
    }



    @Test
    void testStepWithNormDistEvents()
    {
        EventSchedular s = new EventSchedular();

        AtomicInteger i = new AtomicInteger();

        s.addEvent(new NormDistEvent(0, 0, i::getAndIncrement));
        s.addEvent(new NormDistEvent(100, 15, 160, 1, i::getAndIncrement));

        assertEquals(0, i.get());
        s.step(10);
        assertEquals(1, i.get());

        for (int j = 100; j <= 160; j += 15)
        {
            s.step(j);
        }


        assertEquals(5, i.get());
    }
}
