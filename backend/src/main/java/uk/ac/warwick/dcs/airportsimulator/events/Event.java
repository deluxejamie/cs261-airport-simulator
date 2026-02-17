package uk.ac.warwick.dcs.airportsimulator.events;

public class Event {

    Event(double scheduledTime, Runnable action)
    {
        this(scheduledTime, 0, 0, action);
    }

    Event(double scheduledTime, double interval, double endTime, Runnable action)
    {
        this.scheduledTime = scheduledTime;
        this.interval = interval;
        this.endTime = endTime;
        this.action = action;
    }

    public boolean isRecurring()
    {
        return this.interval > 0;
    }

    public void execute()
    {
        action.run();
    }

    public double getScheduledTime()
    {
        return scheduledTime;
    }

    public Event next()
    {
        final double nextTime = scheduledTime + interval;
        if (nextTime > endTime) return null;
        return new Event(nextTime, interval, endTime, action);
    }

    final double scheduledTime;
    final double interval;
    final double endTime;
    final Runnable action;
}
