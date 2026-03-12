package uk.ac.warwick.dcs.airportsimulator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object representing a flight entry received from the frontend.
 *
 * A flight may represent either an arrival or a departure, and may optionally
 * include repeating schedule information.
 */
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

    /**
     * Returns the frontend identifier of the flight.
     *
     * @return flight identifier
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the frontend identifier of the flight.
     *
     * @param id flight identifier
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the flight type, such as arrival or departure.
     *
     * @return flight type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the flight type.
     *
     * @param type flight type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the aircraft callsign.
     *
     * @return aircraft callsign
     */
    public String getCallsign() {
        return callsign;
    }

    /**
     * Sets the aircraft callsign.
     *
     * @param callsign aircraft callsign
     */
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    /**
     * Returns the seed used for this flight configuration.
     *
     * @return random seed
     */
    public Integer getSeed() {
        return seed;
    }

    /**
     * Sets the seed used for this flight configuration.
     *
     * @param seed random seed
     */
    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    /**
     * Returns the expected arrival time for an arrival flight.
     *
     * @return expected arrival time
     */
    public Integer getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    /**
     * Sets the expected arrival time for an arrival flight.
     *
     * @param expectedArrivalTime expected arrival time
     */
    public void setExpectedArrivalTime(Integer expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    /**
     * Returns the expected departure time for a departure flight.
     *
     * @return expected departure time
     */
    public Integer getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    /**
     * Sets the expected departure time for a departure flight.
     *
     * @param expectedDepartureTime expected departure time
     */
    public void setExpectedDepartureTime(Integer expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    /**
     * Returns the emergency status for an arrival flight.
     *
     * @return emergency status
     */
    public String getEmergencyStatus() {
        return emergencyStatus;
    }

    /**
     * Sets the emergency status for an arrival flight.
     *
     * @param emergencyStatus emergency status
     */
    public void setEmergencyStatus(String emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    /**
     * Returns the remaining fuel, in minutes, for an arrival flight.
     *
     * @return remaining fuel in minutes
     */
    public Integer getRemainingFuelMins() {
        return remainingFuelMins;
    }

    /**
     * Sets the remaining fuel, in minutes, for an arrival flight.
     *
     * @param remainingFuelMins remaining fuel in minutes
     */
    public void setRemainingFuelMins(Integer remainingFuelMins) {
        this.remainingFuelMins = remainingFuelMins;
    }

    /**
     * Returns the repeating schedule configuration for this flight.
     *
     * @return repeating configuration
     */
    public RepeatingDto getRepeating() {
        return repeating;
    }

    /**
     * Sets the repeating schedule configuration for this flight.
     *
     * @param repeating repeating configuration
     */
    public void setRepeating(RepeatingDto repeating) {
        this.repeating = repeating;
    }
}