package uk.ac.warwick.dcs.airportsimulator.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftTest {

    @Test
    void constructor_shouldSetAllAttributesCorrectly() {
        // Arrange
        String callSign = "BAW123";
        String operator = "British Airways";
        String origin = "LHR";
        String destination = "EDI";
        double scheduledTime = 100.0;
        int altitude = 30000;
        int groundSpeed = 450;
        double initialFuel = 120.0;
        double fuelBurnRate = 2.0;
        EmergencyStatus emergencyStatus = EmergencyStatus.NONE;
        double timeAddedToSim = 50.0;

        // Act
        Aircraft a = new Aircraft(
                callSign, operator, origin, destination,
                scheduledTime, altitude, groundSpeed,
                initialFuel, fuelBurnRate, emergencyStatus, timeAddedToSim
        );

        // Assert (all getters)
        assertAll(
                () -> assertEquals(callSign, a.getCallSign()),
                () -> assertEquals(operator, a.getOperator()),
                () -> assertEquals(origin, a.getOrigin()),
                () -> assertEquals(destination, a.getDestination()),
                () -> assertEquals(scheduledTime, a.getScheduledTime(), 1e-9),
                () -> assertEquals(altitude, a.getAltitude()),
                () -> assertEquals(groundSpeed, a.getGroundSpeed()),
                () -> assertEquals(emergencyStatus, a.getEmergencyStatus()),
                () -> assertEquals(timeAddedToSim, a.getTimeAddedToSim(), 1e-9)
        );
    }

    @Test
    void getFuelRemaining_shouldReturnInitialFuelAtTimeAdded() {
        Aircraft a = new Aircraft(
                "CS1", "OP", "AAA", "BBB",
                0.0, 0, 0,
                100.0, 5.0, EmergencyStatus.NONE, 10.0
        );

        // simTime == timeAddedToSim => fuelRemaining == initialFuel
        assertEquals(100.0, a.getFuelRemaining(10.0), 1e-9);
    }

    @Test
    void getFuelRemaining_shouldDecreaseLinearlyWithTime() {
        // initialFuel=100, burn=2 per unit time, added at t=10
        Aircraft a = new Aircraft(
                "CS2", "OP", "AAA", "BBB",
                0.0, 0, 0,
                100.0, 2.0, EmergencyStatus.NONE, 10.0
        );

        // at t=15 => 100 - 2*(15-10) = 90
        assertEquals(90.0, a.getFuelRemaining(15.0), 1e-9);

        // at t=25 => 100 - 2*(25-10) = 70
        assertEquals(70.0, a.getFuelRemaining(25.0), 1e-9);
    }

    @Test
    void isFuelCritical_shouldBeFalseWhenFuelIsExactly10() {
        // fuel critical condition is: getFuelRemaining(simTime) < 10
        Aircraft a = new Aircraft(
                "CS3", "OP", "AAA", "BBB",
                0.0, 0, 0,
                20.0, 1.0, EmergencyStatus.NONE, 0.0
        );

        // at t=10 => 20 - 1*(10-0)=10 => NOT critical (strictly < 10)
        assertFalse(a.isFuelCritical(10.0));
        assertEquals(10.0, a.getFuelRemaining(10.0), 1e-9);
    }

    @Test
    void isFuelCritical_shouldBeTrueWhenFuelDropsBelow10() {
        Aircraft a = new Aircraft(
                "CS4", "OP", "AAA", "BBB",
                0.0, 0, 0,
                20.0, 1.0, EmergencyStatus.NONE, 0.0
        );

        // at t=10.1 => fuel = 9.9 => critical
        assertTrue(a.isFuelCritical(10.1));
        assertTrue(a.getFuelRemaining(10.1) < 10.0);
    }
}
