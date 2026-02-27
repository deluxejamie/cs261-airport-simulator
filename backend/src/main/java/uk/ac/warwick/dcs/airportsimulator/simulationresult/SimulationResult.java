package uk.ac.warwick.dcs.airportsimulator.simulator;

/**
 * Collects and stores outcome metrics of a simulation.
 * Fields follow the backend UML.
 */
public final class SimulationResult {

    private int maxTakeOffQueue = 0;
    private double avgTakeOffWait = 0.0;

    private int maxHoldQueue = 0;
    private double avgHoldTime = 0.0;

    private int totalCancellations = 0;
    private int totalDiversions = 0;

    private double avgArrivalDelay = 0.0;
    private double avgDepartureDelay = 0.0;

    private long takeoffCount = 0;
    private double totalTakeoffWait = 0.0;

    private long landingCount = 0;
    private double totalHoldTime = 0.0;

    private long delayedArrivalsCount = 0;
    private double totalArrivalDelay = 0.0;

    private long delayedDeparturesCount = 0;
    private double totalDepartureDelay = 0.0;

    public void recordTakeoffQueueSize(int sz, double simTime) {
        if (sz > maxTakeOffQueue) maxTakeOffQueue = sz;
    }

    public void recordHoldQueueSize(int sz, double simTime) {
        if (sz > maxHoldQueue) maxHoldQueue = sz;
    }

    public void recordCancellation() {
        totalCancellations++;
    }

    public void recordDiversion() {
        totalDiversions++;
    }

    public void recordTakeoffWait(double waitMinutes) {
        takeoffCount++;
        totalTakeoffWait += Math.max(0.0, waitMinutes);
    }

    public void recordHoldTime(double holdMinutes) {
        landingCount++;
        totalHoldTime += Math.max(0.0, holdMinutes);
    }

    public void recordArrivalDelay(double delayMinutes) {
        if (delayMinutes > 0) {
            delayedArrivalsCount++;
            totalArrivalDelay += delayMinutes;
        }
    }

    public void recordDepartureDelay(double delayMinutes) {
        if (delayMinutes > 0) {
            delayedDeparturesCount++;
            totalDepartureDelay += delayMinutes;
        }
    }
    
    public void finalizeAverages() {
        avgTakeOffWait = takeoffCount == 0 ? 0.0 : totalTakeoffWait / takeoffCount;
        avgHoldTime = landingCount == 0 ? 0.0 : totalHoldTime / landingCount;
        avgArrivalDelay = delayedArrivalsCount == 0 ? 0.0 : totalArrivalDelay / delayedArrivalsCount;
        avgDepartureDelay = delayedDeparturesCount == 0 ? 0.0 : totalDepartureDelay / delayedDeparturesCount;
    }

    public int getMaxTakeOffQueue() { return maxTakeOffQueue; }
    public double getAvgTakeOffWait() { return avgTakeOffWait; }

    public int getMaxHoldQueue() { return maxHoldQueue; }
    public double getAvgHoldTime() { return avgHoldTime; }

    public int getTotalCancellations() { return totalCancellations; }
    public int getTotalDiversions() { return totalDiversions; }

    public double getAvgArrivalDelay() { return avgArrivalDelay; }
    public double getAvgDepartureDelay() { return avgDepartureDelay; }
}
