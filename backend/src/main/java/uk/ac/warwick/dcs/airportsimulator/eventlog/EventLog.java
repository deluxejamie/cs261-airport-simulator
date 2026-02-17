package uk.ac.warwick.dcs.airportsimulator.eventlog;

import java.util.ArrayList;
import java.util.List;

public class EventLog {

    EventLog()
    {
        this.logs = new ArrayList<>();
    }

    public void addEntry(EventLogEntry eventLogEntry)
    {
        logs.add(eventLogEntry);
    }

    /**
     * Returns events given an offset and count
     * Note: returns a view to list in this class, hence any modification
     *       will modify in this class as well, so treat as immutable.
     */
    public List<EventLogEntry> getEvents(int offset, int count)
    {
        final int endIndex = Math.min(offset + count, logs.size());
        return logs.subList(offset, endIndex);
    }


    private final List<EventLogEntry> logs;
}
