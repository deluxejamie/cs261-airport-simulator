package uk.ac.warwick.dcs.airportsimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendFlightDto {
    private Integer id;
    private String type;
    private String callsign;
    private Integer seed;

    @JsonProperty("expected_arrival_time")
    private Integer expectedArrivalTime;

    @JsonProperty("expected_departure_time")
    private Integer expectedDepartureTime;

    @JsonProperty("emergency_status")
    private String emergencyStatus;

    @JsonProperty("remaining_fuel_mins")
    private Integer remainingFuelMins;

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

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Integer getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(Integer expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public Integer getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    public void setExpectedDepartureTime(Integer expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    public String getEmergencyStatus() {
        return emergencyStatus;
    }

    public void setEmergencyStatus(String emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    public Integer getRemainingFuelMins() {
        return remainingFuelMins;
    }

    public void setRemainingFuelMins(Integer remainingFuelMins) {
        this.remainingFuelMins = remainingFuelMins;
    }

    public RepeatingDto getRepeating() {
        return repeating;
    }

    public void setRepeating(RepeatingDto repeating) {
        this.repeating = repeating;
    }
}