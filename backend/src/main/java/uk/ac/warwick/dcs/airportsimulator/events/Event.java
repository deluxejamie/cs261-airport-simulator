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
    Event(double scheduledTime, Runnable action)
    {
        this(scheduledTime, 0, 0, action);
    }

    /**
     * Constructs a recuring event
     *
     * @param scheduledTime the time this event should run
     * @param interval      the interval for this event
     * @param endTime       the end time for this recuring event
     * @param action        the action this event should execute
     */
    Event(double scheduledTime, double interval, double endTime, Runnable action)
    {
        this.scheduledTime = scheduledTime;
        this.interval = interval;
        this.endTime = endTime;
        this.action = action;
    }

    /**
     * Returns if the event is a recuring event or not
     *
     * @return if event is recuring
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
    public double getScheduledTime()
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
        final double nextTime = scheduledTime + interval;
        if (nextTime > endTime) return null;
        return new Event(nextTime, interval, endTime, action);
    }

    /* Event scheduled time */
    final double scheduledTime;

    /* Event interval */
    final double interval;
    
    /* Event end time */
    final double endTime;

    /* Event action */
    final Runnable action;
}
