package uk.ac.warwick.dcs.airportsimulator.dto;

/**
 * Data transfer object for advanced simulation configuration parameters.
 *
 * This DTO stores optional global settings that control simulation timing
 * and operational thresholds.
 */
public class AdvancedConfigDto {
    private Integer maxDelayBeforeCancelled;
    private Integer fuelThresholdBeforeRedirected;
    private Integer timeTakenForTakeoff;
    private Integer timeTakenForLanding;

    /**
     * Returns the maximum delay allowed before a departure is cancelled.
     *
     * @return maximum allowed delay before cancellation
     */
    public Integer getMaxDelayBeforeCancelled() {
        return maxDelayBeforeCancelled;
    }

    /**
     * Sets the maximum delay allowed before a departure is cancelled.
     *
     * @param maxDelayBeforeCancelled maximum allowed delay before cancellation
     */
    public void setMaxDelayBeforeCancelled(Integer maxDelayBeforeCancelled) {
        this.maxDelayBeforeCancelled = maxDelayBeforeCancelled;
    }

    /**
     * Returns the fuel threshold below which an arrival may be redirected.
     *
     * @return fuel threshold before redirection
     */
    public Integer getFuelThresholdBeforeRedirected() {
        return fuelThresholdBeforeRedirected;
    }

    /**
     * Sets the fuel threshold below which an arrival may be redirected.
     *
     * @param fuelThresholdBeforeRedirected fuel threshold before redirection
     */
    public void setFuelThresholdBeforeRedirected(Integer fuelThresholdBeforeRedirected) {
        this.fuelThresholdBeforeRedirected = fuelThresholdBeforeRedirected;
    }

    /**
     * Returns the time required for a takeoff operation.
     *
     * @return time taken for takeoff
     */
    public Integer getTimeTakenForTakeoff() {
        return timeTakenForTakeoff;
    }

    /**
     * Sets the time required for a takeoff operation.
     *
     * @param timeTakenForTakeoff time taken for takeoff
     */
    public void setTimeTakenForTakeoff(Integer timeTakenForTakeoff) {
        this.timeTakenForTakeoff = timeTakenForTakeoff;
    }

    /**
     * Returns the time required for a landing operation.
     *
     * @return time taken for landing
     */
    public Integer getTimeTakenForLanding() {
        return timeTakenForLanding;
    }

    /**
     * Sets the time required for a landing operation.
     *
     * @param timeTakenForLanding time taken for landing
     */
    public void setTimeTakenForLanding(Integer timeTakenForLanding) {
        this.timeTakenForLanding = timeTakenForLanding;
    }
}