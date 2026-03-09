package uk.ac.warwick.dcs.airportsimulator.eventlog;

import java.util.HashMap;

/**
 * EventLogEntry class, which stores information about a log
 */
public class EventLogEntry {

    
    /**
     * Constructs a new EventLogEntry with specified info
     *
     * @param type      the type of the event log entry
     * @param timestamp the time of the event log entry
     * @param attr      any extra attributes about the log entry
     */
    public EventLogEntry(EventType type, long timestamp, HashMap<String, Object> attr)
    {
        this.type = type;
        this.timestamp = timestamp;
        this.attr = attr;
    }

    /**
     * Gets type of EventLogEntry
     * @return the type of the event log entry
     */
    public EventType getType() {
        return type;
    }

    /**
     * Gets timestamp of EventLogEntry
     * @return the timestamp of the event log entry
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the attributes of EventLogEntry
     * @return the attributes of the event log entry
     */
    public HashMap<String, Object> getAttr() {
        return attr;
    }

    /* The type of the EventLogEntry*/
    private final EventType type;

    /* The timestamp of the EventLogEntry */
    private final long timestamp;

    /* The attribute of the EventLogEntry */
    private final HashMap<String, Object> attr;
}
