package uk.ac.warwick.dcs.airportsimulator.events;

import java.util.PriorityQueue;


/**
 * EventSchedular handles executing events at the correct time and 
 * in the correct order
 */
public class EventSchedular {

    /**
     * Constructs an empty EventSchedular
     */
    public EventSchedular()
    {
        this.events = new PriorityQueue<IEvent>(
            (a,b) -> Double.compare(a.getScheduledTime(), b.getScheduledTime())
        );
    }

    /**
     * Adds an event to the EventSchedular
     *
     * @param e Event to add
     */
    public void addEvent(IEvent e)
    {
        events.add(e);
    }

    /**
     * Executes all the events up to and including to the current simTime
     * Events are executed in order, such that older events are executed first
     *
     * @param simTime the current simTime
     */
    public void step(int simTime)
    {
        while (!events.isEmpty() && events.peek().getScheduledTime() <= simTime)
        {
            final IEvent e = events.remove();
            e.execute();

            final IEvent next = e.next();
            if (next != null) events.add(next);
        }
    }

    /* Stores events in the correct ordering */
    private final PriorityQueue<IEvent> events;
}
