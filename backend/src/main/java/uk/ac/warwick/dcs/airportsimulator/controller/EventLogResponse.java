package uk.ac.warwick.dcs.airportsimulator.controller;

import java.util.List;

public class EventLogResponse {
    private final List<EventLogItemResponse> events;
    private final long total_events;

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