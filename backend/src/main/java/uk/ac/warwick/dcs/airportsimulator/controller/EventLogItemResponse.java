package uk.ac.warwick.dcs.airportsimulator.controller;

public class EventLogItemResponse {
    private final String eventType;
    private final double simTimestamp;
    private final String attributes;

    public EventLogItemResponse(String eventType, double simTimestamp, String attributes) {
        this.eventType = eventType;
        this.simTimestamp = simTimestamp;
        this.attributes = attributes;
    }

    public String getEventType() {
        return eventType;
    }

    public double getSimTimestamp() {
        return simTimestamp;
    }

    public String getAttributes() {
        return attributes;
    }
}