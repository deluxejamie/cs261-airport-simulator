package uk.ac.warwick.dcs.airportsimulator.entity;

import jakarta.persistence.*;

/**
 * Entity representing the results of a completed simulation.
 *
 * This class stores statistics produced after a simulation
 * run, including queue sizes, waiting times, delays, cancellations,
 * and diversions.
 */

@Entity
@Table(name = "simulation_results")
public class SimulationResultEntity {
    @Id
    private String simulationId;
    private int maxTakeOffQueue;
    private double avgTakeOffWait;
    private int maxHoldQueue;
    private double avgHoldTime;
    private int totalCancellations;
    private int totalDiversions;
    private double avgArrivalDelay;
    private double avgDepartureDelay;

    public SimulationResultEntity() {}

    /**
     * Constructs a simulation result entity with calculated metrics.
     *
     * @param simulationId id of the simulation
     * @param maxTakeOffQueue max take off queue length
     * @param avgTakeOffWait avg waiting time in the take off queue
     * @param maxHoldQueue max holding pattern size
     * @param avgHoldTime avg holding time for arrivals
     * @param totalCancellations no. of cancelled departures
     * @param totalDiversions no. of diverted aircraft
     * @param avgArrivalDelay avg arrival delay
     * @param avgDepartureDelay avg departure delay
     */

    public SimulationResultEntity(String simulationId, int maxTakeOffQueue, double avgTakeOffWait,
                                   int maxHoldQueue, double avgHoldTime, int totalCancellations,
                                   int totalDiversions, double avgArrivalDelay, double avgDepartureDelay) {
        this.simulationId = simulationId;
        this.maxTakeOffQueue = maxTakeOffQueue;
        this.avgTakeOffWait = avgTakeOffWait;
        this.maxHoldQueue = maxHoldQueue;
        this.avgHoldTime = avgHoldTime;
        this.totalCancellations = totalCancellations;
        this.totalDiversions = totalDiversions;
        this.avgArrivalDelay = avgArrivalDelay;
        this.avgDepartureDelay = avgDepartureDelay;
    }

    public String getSimulationId() { return simulationId; }
    public int getMaxTakeOffQueue() { return maxTakeOffQueue; }
    public double getAvgTakeOffWait() { return avgTakeOffWait; }
    public int getMaxHoldQueue() { return maxHoldQueue; }
    public double getAvgHoldTime() { return avgHoldTime; }
    public int getTotalCancellations() { return totalCancellations; }
    public int getTotalDiversions() { return totalDiversions; }
    public double getAvgArrivalDelay() { return avgArrivalDelay; }
    public double getAvgDepartureDelay() { return avgDepartureDelay; }
}