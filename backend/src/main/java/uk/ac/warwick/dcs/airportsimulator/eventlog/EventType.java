package uk.ac.warwick.dcs.airportsimulator.eventlog;

/**
 * Event type enum containing all event types
 * which will be stored in the event log
 */
public enum EventType {
    LANDING_EVENT,
    TAKEOFF_EVENT,
    HOLDING_EVENT,
    DIVERSION_EVENT,
    EMERGENCY_EVENT,
    RUNWAY_ZONE_EVENT,
    RUNWAY_MODE_EVENT,
    CANCELLATION_EVENT,
    RUNWAY_STATUS_EVENT
}
