package uk.ac.warwick.dcs.airportsimulator.dto;

public class AdvancedConfigDto {
    private Integer maxDelayBeforeCancelled;
    private Integer fuelThresholdBeforeRedirected;
    private Integer timeTakenForTakeoff;
    private Integer timeTakenForLanding;

    public Integer getMaxDelayBeforeCancelled() {
        return maxDelayBeforeCancelled;
    }

    public void setMaxDelayBeforeCancelled(Integer maxDelayBeforeCancelled) {
        this.maxDelayBeforeCancelled = maxDelayBeforeCancelled;
    }

    public Integer getFuelThresholdBeforeRedirected() {
        return fuelThresholdBeforeRedirected;
    }

    public void setFuelThresholdBeforeRedirected(Integer fuelThresholdBeforeRedirected) {
        this.fuelThresholdBeforeRedirected = fuelThresholdBeforeRedirected;
    }

    public Integer getTimeTakenForTakeoff() {
        return timeTakenForTakeoff;
    }

    public void setTimeTakenForTakeoff(Integer timeTakenForTakeoff) {
        this.timeTakenForTakeoff = timeTakenForTakeoff;
    }

    public Integer getTimeTakenForLanding() {
        return timeTakenForLanding;
    }

    public void setTimeTakenForLanding(Integer timeTakenForLanding) {
        this.timeTakenForLanding = timeTakenForLanding;
    }
}