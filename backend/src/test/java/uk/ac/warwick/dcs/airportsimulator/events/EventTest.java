package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;

import java.io.Console;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void isRecurring_shouldBeFalseWhenIntervalIsZero() {
        PlainEvent e = new PlainEvent(10, () -> {});
        assertFalse(e.isRecurring());
    }

    @Test
    void isRecurring_shouldBeTrueWhenIntervalGreaterThanZero() {
        PlainEvent e = new PlainEvent(10, 5, 50, () -> {});
        assertTrue(e.isRecurring());
    }

    @Test
    void execute_shouldRunAction() {
        AtomicInteger counter = new AtomicInteger(0);
        PlainEvent e = new PlainEvent(0, counter::incrementAndGet);

        e.execute();

        assertEquals(1, counter.get());
    }

    @Test
    void next_shouldReturnNullForNonRecurringEvent() {
        PlainEvent e = new PlainEvent(10, () -> {});
        assertNull(e.next());
    }

    @Test
    void next_shouldCreateNextEventUntilEndTime() {
        AtomicInteger counter = new AtomicInteger(0);
        PlainEvent e = new PlainEvent(10, 5, 25, counter::incrementAndGet);

        IEvent n1 = e.next();
        assertNotNull(n1);
        assertEquals(15.0, n1.getScheduledTime(), 1e-9);

        IEvent n2 = n1.next();
        assertNotNull(n2);
        assertEquals(20.0, n2.getScheduledTime(), 1e-9);

        IEvent n3 = n2.next();
        assertNull(n3);
    }

    @Test
    void testNormDistTestSingle()
    {
        final int seed = new Random().nextInt();
        final int correctScheduledTime = new Random().nextInt();
        NormDistEvent nde = new NormDistEvent(correctScheduledTime, seed, ()->{});

        assertEquals(nde.getScheduledTime(), (int) new Random(seed).nextGaussian(correctScheduledTime, 5));
    }

    @Test
    void testNormDistTestRecurring()
    {
        final int seed = new Random().nextInt();
        int correctScheduledTime = new Random().nextInt();

        final Random rng = new Random(seed);

        IEvent nde = new NormDistEvent(correctScheduledTime, 15, correctScheduledTime + 60 * 10, seed, ()->{});

        while (nde != null)
        {
            assertEquals(nde.getScheduledTime(), (int) rng.nextGaussian(correctScheduledTime, 5));
            correctScheduledTime += 15;
            nde = nde.next();
        }
    }
}


