package uk.ac.warwick.dcs.airportsimulator.controller;

import java.util.List;

/**
 * EventLogResponse for frontend to consume
 */
public class EventLogResponse {
    /**
     * Private fields
     */
    private final List<EventLogItemResponse> events;
    private final long total_events;

    /**
     * Constructs an EventLogResponse
     * @param events       the events
     * @param total_events the total number of events
     */
    public EventLogResponse(List<EventLogItemResponse> events, long total_events) {
        this.events=events;
        this.total_events=total_events;
    }

    public List<EventLogItemResponse> getEvents() {
        return events;
    }

    public long getTotal_events() {
        return total_events;
    }
}