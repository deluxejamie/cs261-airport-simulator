package uk.ac.warwick.dcs.airportsimulator.aircraft;

/**
 * Aircraft class describes an aircraft in the simulation
 */
public class Aircraft {

    /**
     * Constructs a new Aircraft with specified information
     *
     * @param callSign        the aircraft's callSign
     * @param operator        the aircraft's operator
     * @param origin          the aircraft's origin
     * @param destination     the aircraft's destination
     * @param scheduledTime   the aircraft's scheduledTime
     * @param altitude        the aircraft's altitude
     * @param groundSpeed     the aircraft's groundSpeed
     * @param initialFuel     the aircraft's initialFuel
     * @param fuelBurnRate    the aircraft's fuelBurnRate
     * @param emergencyStatus the aircraft's emergencyStatus
     * @param timeAddedToSim  the time the aircraft was added to the sim 
     */
    Aircraft(String callSign, String operator, String origin, String destination, double scheduledTime, int altitude, int groundSpeed, double initialFuel, double fuelBurnRate, EmergencyStatus emergencyStatus, double simTime) {
        this.callSign = callSign;
        this.operator = operator;
        this.origin = origin;
        this.destination = destination;
        this.scheduledTime = scheduledTime;
        this.altitude = altitude;
        this.groundSpeed = groundSpeed;
        this.initialFuel = initialFuel;
        this.fuelBurnRate = fuelBurnRate;
        this.emergencyStatus = emergencyStatus;
        this.timeAddedToSim = simTime;
    }

    /**
     * Gets the fuel remaining for the aircraft
     *
     * @param simTime the current time in the sim
     * @return the fuel remaining
     */
    public double getFuelRemaining(double simTime) {
        return initialFuel - (fuelBurnRate * (simTime - timeAddedToSim));
    }

    /**
     * Gets whether the aircraft fuel is critical
     *
     * @param simTime the current time in the sim
     * @return if the fuel is critical
     */
    public boolean isFuelCritical(double simTime) {
        return getFuelRemaining(simTime) < 10;
    }

    /**
     * Gets the aircaft's callsign
     * @return the aircraft's callsign
     */
    public String getCallSign() {
        return callSign;
    }

    /**
     * Gets the aircaft's operator
     * @return the aircraft's operator
     */
    public String getOperator() {
        return operator;
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
    public double getScheduledTime() {
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

    /**
     * Gets the time the aircraft awas added to the sim
     * @return the time the aircraft was added to the sim
     */
    public double getTimeAddedToSim() {
        return timeAddedToSim;
    }

    /* The aircraft's initialFuel */
    private final double initialFuel;

    /* The aircraft's fuelBurnRate */
    private final double fuelBurnRate;

    /* The aircraft's callSign */
    private final String callSign;

    /* The aircraft's operator */
    private final String operator;

    /* The aircraft's origin */
    private final String origin;

    /* The aircraft's destination */
    private final String destination;

    /* The aircraft's scheduledTime */
    private final double scheduledTime;

    /* The aircraft's altitude */
    private final int altitude;

    /* The aircraft's groundSpeed */
    private final int groundSpeed;

    /* The aircraft's emergencyStatus */
    private final EmergencyStatus emergencyStatus;

    /* The time the aircraft was added to the sim */
    private final double timeAddedToSim;
}
