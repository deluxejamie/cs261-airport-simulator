package uk.ac.warwick.dcs.airportsimulator.eventlog;

import java.util.HashMap;

public class EventLogEntry {

    EventLogEntry(EventType type, double timestamp, HashMap<String, Object> attr)
    {
        this.type = type;
        this.timestamp = timestamp;
        this.attr = attr;
    }

    public EventType getType() {
        return type;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public HashMap<String, Object> getAttr() {
        return attr;
    }

    private final EventType type;
    private final double timestamp;
    private final HashMap<String, Object> attr;
}
