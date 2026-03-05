package uk.ac.warwick.dcs.airportsimulator.aircraft;

/**
 * Aircraft class describes an aircraft in the simulation
 */
public class Aircraft {

    /**
     * Constructs a new Aircraft with specified information
     *
     * @param callSign        the aircraft's callSign
     * @param origin          the aircraft's origin
     * @param destination     the aircraft's destination
     * @param scheduledTime   the aircraft's scheduledTime
     * @param altitude        the aircraft's altitude
     * @param groundSpeed     the aircraft's groundSpeed
     * @param initialFuel     the aircraft's initialFuel (in minutes)
     * @param emergencyStatus the aircraft's emergencyStatus
     * @param timeAddedToSim  the time the aircraft was added to the sim 
     */
    public Aircraft(String callSign, String origin, String destination, int scheduledTime, int altitude, int groundSpeed, int initialFuel, EmergencyStatus emergencyStatus, int timeAddedToSim) {
        this.callSign = callSign;
        this.origin = origin;
        this.destination = destination;
        this.scheduledTime = scheduledTime;
        this.altitude = altitude;
        this.groundSpeed = groundSpeed;
        this.initialFuel = initialFuel;
        this.emergencyStatus = emergencyStatus;
        this.timeFuelRunsOut = initialFuel + timeAddedToSim;
    }

    /**
     * Gets the fuel remaining for the aircraft
     *
     * @param simTime the current time in the sim
     * @return the fuel remaining
     */
    public int getFuelRemaining(int simTime) {
        return timeFuelRunsOut - simTime;
    }

    /**
     * Gets whether the aircraft fuel is critical
     *
     * @param simTime the current time in the sim
     * @return if the fuel is critical
     */
    public boolean isFuelCritical(int simTime) {
        return getFuelRemaining(simTime) < 10;
    }

    /**
     * Gets the aircraft's initial fuel when
     * entering the simulation
     * @return the initial fuel of the aircraft
     */
    public int getInitialFuel() {
        return initialFuel;
    }

    /**
     * Gets the aircaft's callsign
     * @return the aircraft's callsign
     */
    public String getCallSign() {
        return callSign;
    }

    /**
     * Gets the aircaft's origin
     * @return the aircraft's origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Gets the aircaft's destination
     * @return the aircraft's destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Gets the aircaft's scheduled time
     * @return the aircraft's scheduled time
     */
    public int getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Gets the aircaft's altitude
     * @return the aircraft's altitude
     */
    public int getAltitude() {
        return altitude;
    }

    /**
     * Gets the aircaft's ground speed
     * @return the aircraft's ground speed
     */
    public int getGroundSpeed() {
        return groundSpeed;
    }

    /**
     * Gets the aircaft's emergency status
     * @return the aircraft's emergency status
     */
    public EmergencyStatus getEmergencyStatus() {
        return emergencyStatus;
    }

    public void setEmergencyStatus(EmergencyStatus emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    /* The aircraft's initialFuel (in minutes) */
    private final int initialFuel;

    /* The aircraft's callSign */
    private final String callSign;

    /* The aircraft's origin */
    private final String origin;

    /* The aircraft's destination */
    private final String destination;

    /* The aircraft's scheduledTime */
    private final int scheduledTime;

    /* The aircraft's altitude */
    private final int altitude;

    /* The aircraft's groundSpeed */
    private final int groundSpeed;

    /* The aircraft's emergencyStatus */
    private EmergencyStatus emergencyStatus;

    /* The time the aircraft has no fuel remaining */
    private final int timeFuelRunsOut;
}
