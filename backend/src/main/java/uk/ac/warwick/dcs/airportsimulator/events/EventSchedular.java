package uk.ac.warwick.dcs.airportsimulator.events;

import java.util.PriorityQueue;

public class EventSchedular {

    public void addEvent(Event e)
    {
        events.add(e);
    }

    public void step(double simTime)
    {
        while (!events.isEmpty() && events.peek().getScheduledTime() <= simTime)
        {
            final Event e = events.remove();
            e.execute();

            final Event next = e.next();
            if (next != null) events.add(next);
        }
    }

    private PriorityQueue<Event> events;
}
