package uk.ac.warwick.dcs.airportsimulator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a simulation record stored in the database.
 *
 * This entity stores metadata about a simulation run, including
 * its unique identifier, current status, configuration used to run
 * the simulation, and the time at which it was created.
 *
 * The configuration is stored as a JSON string in the database.
 */

@Entity
@Table(name = "simulations")
public class SimulationEntity {
    @Id
    private String id;
    private String status;
    @Column(columnDefinition = "TEXT")
    private String config;
    private LocalDateTime createdAt;

    public SimulationEntity() {}

    /**
     * Constructs a new simulation entity with the given parameters.
     * @param id unique identifier for the simulation
     * @param status current status of the simulation
     * @param config JSON configuration to initialise the simulation
     */

    public SimulationEntity(String id, String status, String config) {
        this.id = id;
        this.status = status;
        this.config = config;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getConfig() { return config; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}