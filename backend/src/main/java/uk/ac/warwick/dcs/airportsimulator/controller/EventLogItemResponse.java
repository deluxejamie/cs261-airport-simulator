package uk.ac.warwick.dcs.airportsimulator.controller;

/**
 * Event Log Item Response for frontend to consume
 */
public class EventLogItemResponse {
    /**
     * Private fields
     */
    private final String eventType;
    private final double simTimestamp;
    private final String attributes;

    /**
     * Constructs EventLogItemResponse
     * @param eventType    event type
     * @param simTimestamp sim timestamp
     * @param attributes   attributes
     */
    public EventLogItemResponse(String eventType, double simTimestamp, String attributes) {
        this.eventType = eventType;
        this.simTimestamp = simTimestamp;
        this.attributes = attributes;
    }

    /**
     * @return Event type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * @return sim timestamp
     */
    public double getSimTimestamp() {
        return simTimestamp;
    }

    /**
     * @return attributes
     */
    public String getAttributes() {
        return attributes;
    }
}