package uk.ac.warwick.dcs.airportsimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "event_log_entries")
public class EventLogEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String simulationId;
    private String eventType;
    private double simTimestamp;
    @Column(columnDefinition = "TEXT")
    private String attributes; // JSON string

    public EventLogEntryEntity() {}
    public EventLogEntryEntity(String simulationId, String eventType, double simTimestamp, String attributes) {
        this.simulationId = simulationId;
        this.eventType = eventType;
        this.simTimestamp = simTimestamp;
        this.attributes = attributes;
    }

    public Long getId() { return id; }
    public String getSimulationId() { return simulationId; }
    public String getEventType() { return eventType; }
    public double getSimTimestamp() { return simTimestamp; }
    public String getAttributes() { return attributes; }
}