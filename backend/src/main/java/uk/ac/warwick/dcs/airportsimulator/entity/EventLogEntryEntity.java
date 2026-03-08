package uk.ac.warwick.dcs.airportsimulator.entity;

import jakarta.persistence.*;

/**
 * Entity representing a logged event generated during a simulation.
 *
 * Each entry corresponds to a specific event within the
 * simulation timeline.
 * Additional event details are stored as a JSON string in the
 * attributes field.
 */

@Entity
@Table(name = "event_log_entries")
public class EventLogEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String simulationId;
    private String eventType;
    private int simTimestamp;
    @Column(columnDefinition = "TEXT")
    private String attributes; // JSON string

    public EventLogEntryEntity() {}

    /**
     * Constructs a new event log entry.
     *
     * @param simulationId identifier of the simulation
     * @param eventType type of event being recorded
     * @param simTimestamp simulation time when the event occurred
     * @param attributes JSON string containing additional event details
     */

    public EventLogEntryEntity(String simulationId, String eventType, int simTimestamp, String attributes) {
        this.simulationId = simulationId;
        this.eventType = eventType;
        this.simTimestamp = simTimestamp;
        this.attributes = attributes;
    }

    public Long getId() { return id; }
    public String getSimulationId() { return simulationId; }
    public String getEventType() { return eventType; }
    public int getSimTimestamp() { return simTimestamp; }
    public String getAttributes() { return attributes; }
}