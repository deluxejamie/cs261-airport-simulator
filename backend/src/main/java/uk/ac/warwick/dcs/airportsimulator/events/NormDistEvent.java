package uk.ac.warwick.dcs.airportsimulator.events;

import java.io.Console;
import java.util.Random;

public class NormDistEvent implements IEvent {
    /**
     * Constructs a one time event
     *
     * @param correctScheduledTime the time this event should run
     * @param seed                 the seed for rng generator
     * @param action               the action this event should execute
     */
    public NormDistEvent(int correctScheduledTime, long seed, Runnable action)
    {
        this(correctScheduledTime, 0, 0, seed, action);
    }

    /**
     * Constructs a recurring event
     *
     * @param correctScheduledTime the time this event should run
     * @param interval      the interval for this event
     * @param endTime       the end time for this recurring event
     * @param seed                 the seed for rng generator
     * @param action        the action this event should execute
     */
    public NormDistEvent(int correctScheduledTime, int interval, int endTime, long seed, Runnable action) {
        this(correctScheduledTime, interval, endTime, action, new Random(seed) );
    }

    /**
     * Constructs a recurring event
     *
     * @param correctScheduledTime the time this event should run
     * @param interval      the interval for this event
     * @param endTime       the end time for this recurring event
     * @param action        the action this event should execute
     * @param rng           the random number generator
     */
    private NormDistEvent(int correctScheduledTime, int interval, int endTime, Runnable action, Random rng)
    {
        this.scheduledTime = (int) rng.nextGaussian(correctScheduledTime, 5);
        this.correctScheduledTime = correctScheduledTime;
        this.interval = interval;
        this.endTime = endTime;
        this.action = action;
        this.rng = rng;
    }

    /**
     * Returns if the event is a recurring event or not
     *
     * @return if event is recurring
     */
    @Override
    public boolean isRecurring() {
        return this.interval > 0;
    }

    /**
     * Executes the event
     */
    @Override
    public void execute() {
        action.run();
    }

    /**
     * Gets the next scheduled time for the event
     *
     * @return the next scheduled time
     */
    @Override
    public int getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Generate the next event (if there is one)
     * Returns null if the current event is final one
     *
     * @return the next event (or null)
     */
    @Override
    public IEvent next() {
        final int nextTime = correctScheduledTime + interval;
        if (nextTime >= endTime) return null;
        return new NormDistEvent(nextTime, interval, endTime, action, rng);
    }


    /* Event scheduled time */
    final int scheduledTime;

    /* Event correct schedule time */
    final int correctScheduledTime;

    /* Event interval */
    final int interval;

    /* Event end time */
    final int endTime;

    /* Event action */
    final Runnable action;

    /* Random number generator */
    final Random rng;
}
