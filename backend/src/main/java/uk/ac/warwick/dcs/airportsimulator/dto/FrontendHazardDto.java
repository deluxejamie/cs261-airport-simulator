package uk.ac.warwick.dcs.airportsimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object representing a hazard event submitted from the frontend.
 *
 * Hazards may represent runway closures or emergency events affecting aircraft.
 */
public class FrontendHazardDto {
    private Integer id;
    private String type;

    @JsonProperty("affected_runway")
    private Integer affectedRunway;

    @JsonProperty("closure_mode")
    private String closureMode;

    @JsonProperty("duration_mins")
    private Integer durationMins;

    private Integer time;

    @JsonProperty("target_arrival_callsign")
    private String targetArrivalCallsign;

    @JsonProperty("emergency_type")
    private String emergencyType;

    private RepeatingDto repeating;

    /**
     * Returns the frontend identifier of the hazard.
     *
     * @return hazard identifier
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the frontend identifier of the hazard.
     *
     * @param id hazard identifier
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the hazard type.
     *
     * @return hazard type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the hazard type.
     *
     * @param type hazard type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the runway affected by the hazard.
     *
     * @return affected runway identifier
     */
    public Integer getAffectedRunway() {
        return affectedRunway;
    }

    /**
     * Sets the runway affected by the hazard.
     *
     * @param affectedRunway affected runway identifier
     */
    public void setAffectedRunway(Integer affectedRunway) {
        this.affectedRunway = affectedRunway;
    }

    /**
     * Returns the runway closure mode.
     *
     * @return closure mode
     */
    public String getClosureMode() {
        return closureMode;
    }

    /**
     * Sets the runway closure mode.
     *
     * @param closureMode closure mode
     */
    public void setClosureMode(String closureMode) {
        this.closureMode = closureMode;
    }

    /**
     * Returns the hazard duration in minutes.
     *
     * @return duration in minutes
     */
    public Integer getDurationMins() {
        return durationMins;
    }

    /**
     * Sets the hazard duration in minutes.
     *
     * @param durationMins duration in minutes
     */
    public void setDurationMins(Integer durationMins) {
        this.durationMins = durationMins;
    }

    /**
     * Returns the scheduled time of the hazard event.
     *
     * @return event time
     */
    public Integer getTime() {
        return time;
    }

    /**
     * Sets the scheduled time of the hazard event.
     *
     * @param time event time
     */
    public void setTime(Integer time) {
        this.time = time;
    }

    /**
     * Returns the callsign of the arrival aircraft targeted by the hazard.
     *
     * @return target arrival callsign
     */
    public String getTargetArrivalCallsign() {
        return targetArrivalCallsign;
    }

    /**
     * Sets the callsign of the arrival aircraft targeted by the hazard.
     *
     * @param targetArrivalCallsign target arrival callsign
     */
    public void setTargetArrivalCallsign(String targetArrivalCallsign) {
        this.targetArrivalCallsign = targetArrivalCallsign;
    }

    /**
     * Returns the emergency type associated with this hazard.
     *
     * @return emergency type
     */
    public String getEmergencyType() {
        return emergencyType;
    }

    /**
     * Sets the emergency type associated with this hazard.
     *
     * @param emergencyType emergency type
     */
    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    /**
     * Returns the repeating schedule configuration for this hazard.
     *
     * @return repeating configuration
     */
    public RepeatingDto getRepeating() {
        return repeating;
    }

    /**
     * Sets the repeating schedule configuration for this hazard.
     *
     * @param repeating repeating configuration
     */
    public void setRepeating(RepeatingDto repeating) {
        this.repeating = repeating;
    }
}