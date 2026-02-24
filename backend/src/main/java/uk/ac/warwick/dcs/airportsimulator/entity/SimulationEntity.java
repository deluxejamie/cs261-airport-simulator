package uk.ac.warwick.dcs.airportsimulator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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