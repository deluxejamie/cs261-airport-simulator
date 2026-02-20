package uk.ac.warwick.dcs.airportsimulator.runway;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;

public class Runway {

    Runway(int runwayNumber, double length, double bearing, RunwayMode mode, RunwayStatus status, Aircraft occupied)
    {
        this.runwayNumber = runwayNumber;
        this.length = length;
        this.bearing = bearing;
        this.mode = mode;
        this.status = status;
        this.occupied = occupied;
    }

    public void setMode(RunwayMode mode) {
        this.mode = mode;
    }

    public void setStatus(RunwayStatus status) {
        this.status = status;
    }

    public void setOccupied(Aircraft aircraft)
    {
        this.occupied = aircraft;
    }

    public RunwayMode getMode() {
        return mode;
    }

    public RunwayStatus getStatus() {
        return status;
    }

    public int getRunwayNumber() {
        return runwayNumber;
    }

    public double getLength() {
        return length;
    }

    public double getBearing() {
        return bearing;
    }

    public Aircraft getOccupied() {
        return occupied;
    }

    private RunwayMode mode;
    private RunwayStatus status;
    private final int runwayNumber;
    private final double length;
    private final double bearing;
    private Aircraft occupied;
}
