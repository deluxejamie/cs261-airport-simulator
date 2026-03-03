package uk.ac.warwick.dcs.airportsimulator.runway;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;


/**
 * Runway class describes a runway in the simulation
 */
public class Runway {


    /**
     * Constructs a runway with given parameters
     * 
     * @param runwayNumber  the runway's runwayNumber
     * @param length  the runway's length
     * @param bearing  the runway's bearing
     * @param mode  the runway's mode
     * @param status  the runway's status
     * @param occupied whether the runway is occupied
     */
    public Runway(int runwayNumber, double length, double bearing, RunwayMode mode, RunwayStatus status, Aircraft occupied)
    {
        this.runwayNumber = runwayNumber;
        this.length = length;
        this.bearing = bearing;
        this.mode = mode;
        this.status = status;
        this.occupied = occupied;
    }

    /**
     * Sets the mode of the runway
     * 
     * @param mode the new mode to set the runway to
     */
    public void setMode(RunwayMode mode) {
        this.mode = mode;
    }

    /**
     * Sets the status of the runway
     * 
     * @param status the new status to set the runway to
     */
    public void setStatus(RunwayStatus status) {
        this.status = status;
    }

    /**
     * Sets whether the runway is occupied
     * 
     * @param aircraft the aircraft to occupy the runway with
     */
    public void setOccupied(Aircraft aircraft)
    {
        this.occupied = aircraft;
    }

    /**
     * Gets the runway mode
     *
     * @return the runway mode
     */
    public RunwayMode getMode() {
        return mode;
    }

    /**
     * Gets the runway status
     *
     * @return the runway status
     */
    public RunwayStatus getStatus() {
        return status;
    }

    /**
     * Gets the runway number
     *
     * @return the runway number
     */
    public int getRunwayNumber() {
        return runwayNumber;
    }

    /**
     * Gets the runway length
     *
     * @return the runway length
     */
    public double getLength() {
        return length;
    }

    /**
     * Gets the runway bearing
     *
     * @return the runway bearing
     */
    public double getBearing() {
        return bearing;
    }

    /**
     * Gets whether the runway is occupied
     *
     * @return the aircraft that is occupying the runway
     */
    public Aircraft getOccupied() {
        return occupied;
    }

    /* The runway's mode */
    private RunwayMode mode;

    /* The runway's status */
    private RunwayStatus status;

    /* The runway's int runwayNumber */
    private final int runwayNumber;

    /* The runway's double length */
    private final double length;

    /* The runway's double bearing */
    private final double bearing;

    /* The aircraft occupying the runway */
    private Aircraft occupied;
}
