package uk.ac.warwick.dcs.airportsimulator.events;


/**
 * Event class
 *
 * Allows an action to be run every interval unit of time until the endTime 
 * is reached.
 */
public class Event {

    /**
     * Constructs a one time event 
     *
     * @param scheduledTime the time this event should run
     * @param action        the action this event should execute 
     */
    Event(int scheduledTime, Runnable action)
    {
        this(scheduledTime, 0, 0, action);
    }

    /**
     * Constructs a recurring event
     *
     * @param scheduledTime the time this event should run
     * @param interval      the interval for this event
     * @param endTime       the end time for this recurring event
     * @param action        the action this event should execute
     */
    Event(int scheduledTime, int interval, int endTime, Runnable action)
    {
        this.scheduledTime = scheduledTime;
        this.interval = interval;
        this.endTime = endTime;
        this.action = action;
    }

    /**
     * Returns if the event is a recurring event or not
     *
     * @return if event is recurring
     */
    public boolean isRecurring()
    {
        return this.interval > 0;
    }

    /**
     * Executes the event
     */
    public void execute()
    {
        action.run();
    }

    /**
     * Gets the next scheduled time for the event
     *
     * @return the next scheduled time
     */
    public int getScheduledTime()
    {
        return scheduledTime;
    }

    /**
     * Generate the next event (if there is one)
     * Returns null if the current event is final one
     *
     * @return the next event (or null)
     */
    public Event next()
    {
        final int nextTime = scheduledTime + interval;
        if (nextTime > endTime) return null;
        return new Event(nextTime, interval, endTime, action);
    }

    /* Event scheduled time */
    final int scheduledTime;

    /* Event interval */
    final int interval;
    
    /* Event end time */
    final int endTime;

    /* Event action */
    final Runnable action;
}
