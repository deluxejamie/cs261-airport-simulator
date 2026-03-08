package uk.ac.warwick.dcs.airportsimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    private RepeatingDto repeating;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAffectedRunway() {
        return affectedRunway;
    }

    public void setAffectedRunway(Integer affectedRunway) {
        this.affectedRunway = affectedRunway;
    }

    public String getClosureMode() {
        return closureMode;
    }

    public void setClosureMode(String closureMode) {
        this.closureMode = closureMode;
    }

    public Integer getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(Integer durationMins) {
        this.durationMins = durationMins;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getTargetArrivalCallsign() {
        return targetArrivalCallsign;
    }

    public void setTargetArrivalCallsign(String targetArrivalCallsign) {
        this.targetArrivalCallsign = targetArrivalCallsign;
    }

    public RepeatingDto getRepeating() {
        return repeating;
    }

    public void setRepeating(RepeatingDto repeating) {
        this.repeating = repeating;
    }
}