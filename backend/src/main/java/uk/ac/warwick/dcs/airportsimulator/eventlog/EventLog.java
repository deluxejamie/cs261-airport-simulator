package uk.ac.warwick.dcs.airportsimulator.eventlog;

import java.util.ArrayList;
import java.util.List;

/**
 * EventLog class stores all the EventLogEntries that
 * occur throughout the simulation
 */
public class EventLog {

    /**
     * Constructs an empty EventLog
     */
    public EventLog()
    {
        this.logs = new ArrayList<>();
    }

    /**
     * Adds an EventLogEntry to the EventLog
     *
     * @param eventLogEntry the eventLogEntry to add to the EventLog
     */
    public void addEntry(EventLogEntry eventLogEntry)
    {
        logs.add(eventLogEntry);
    }

    /**
     * Returns events given an offset and count
     * Note: returns a view to list in this class, hence any modification
     *       will modify in this class as well, so treat as immutable.
     *
     * @param offset the offset in the event log
     * @param count  the number of eventLogEntries to take
     * @return       a view of the list containing count entries from the offset
     */
    public List<EventLogEntry> getEvents(int offset, int count)
    {
        final int endIndex = Math.min(offset + count, logs.size());
        return logs.subList(offset, endIndex);
    }

    /* List of EventLogEntry */
    private final List<EventLogEntry> logs;
}
