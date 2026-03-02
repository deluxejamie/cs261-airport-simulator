package uk.ac.warwick.dcs.airportsimulator.events;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class NormDistEventTest {

    @Test
    void oneShotEvent_shouldUseGaussianAroundCorrectTime_deterministicWithSeed() {
        int correctTime = 100;
        long seed = 42L;

        // expected scheduledTime = (int) rng.nextGaussian(correctTime, 5)
        Random rng = new Random(seed);
        int expected = (int) rng.nextGaussian(correctTime, 5);

        NormDistEvent e = new NormDistEvent(correctTime, seed, () -> {});

        assertFalse(e.isRecurring());
        assertEquals(expected, e.getScheduledTime());
        assertNull(e.next());
    }

    @Test
    void execute_shouldRunAction() {
        AtomicInteger counter = new AtomicInteger(0);
        NormDistEvent e = new NormDistEvent(0, 123L, counter::incrementAndGet);

        e.execute();

        assertEquals(1, counter.get());
    }

    @Test
    void recurringEvent_nextShouldAdvanceCorrectScheduledTime_andReuseSameRngStream() {
        int start = 100;
        int interval = 10;
        int endTime = 131; // nextTime>=endTime stops; start=100 => next correct times: 110, 120, 130 then stop at 140
        long seed = 7L;

        // Reproduce RNG stream: constructor consumes one gaussian, next() consumes the next gaussian, etc.
        Random rng = new Random(seed);
        int expectedFirstScheduled = (int) rng.nextGaussian(start, 5);
        int nextCorrectTime = start + interval;
        int expectedSecondScheduled = (int) rng.nextGaussian(nextCorrectTime, 5);

        NormDistEvent e = new NormDistEvent(start, interval, endTime, seed, () -> {});
        assertTrue(e.isRecurring());
        assertEquals(expectedFirstScheduled, e.getScheduledTime());

        IEvent next = e.next();
        assertNotNull(next);
        assertEquals(expectedSecondScheduled, next.getScheduledTime());

        // Boundary: when correctScheduledTime+interval >= endTime => null
        // Walk until correctScheduledTime reaches 130, then next correct time 140 >= 131 => null
        IEvent cur = next;
        while (cur != null && cur.getScheduledTime() != Integer.MIN_VALUE) {
            IEvent maybe = cur.next();
            // We don't need exact gaussian values here; we just ensure termination happens.
            // Stop when next() returns null.
            if (maybe == null) break;
            cur = maybe;
        }
        assertNull(cur.next()); // eventually should terminate
    }
}