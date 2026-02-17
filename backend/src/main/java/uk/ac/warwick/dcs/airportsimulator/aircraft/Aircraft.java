package uk.ac.warwick.dcs.airportsimulator.aircraft;

public class Aircraft {

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

    public double getFuelRemaining(double simTime) {
        return initialFuel - (fuelBurnRate * (simTime - timeAddedToSim));
    }

    public boolean isFuelCritical(double simTime) {
        return getFuelRemaining(simTime) < 10;
    }

    public String getCallSign() {
        return callSign;
    }

    public String getOperator() {
        return operator;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getScheduledTime() {
        return scheduledTime;
    }

    public int getAltitude() {
        return altitude;
    }

    public int getGroundSpeed() {
        return groundSpeed;
    }

    public EmergencyStatus getEmergencyStatus() {
        return emergencyStatus;
    }

    public double getTimeAddedToSim() {
        return timeAddedToSim;
    }

    private final double initialFuel;
    private final double fuelBurnRate;
    private final String callSign;
    private final String operator;
    private final String origin;
    private final String destination;
    private final double scheduledTime;
    private final int altitude;
    private final int groundSpeed;
    private final EmergencyStatus emergencyStatus;
    private final double timeAddedToSim;
}
