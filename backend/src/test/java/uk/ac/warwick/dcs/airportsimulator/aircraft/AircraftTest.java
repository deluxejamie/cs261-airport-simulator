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
        int scheduledTime = 100;
        int altitude = 30000;
        int groundSpeed = 450;
        int initialFuel = 120;
        EmergencyStatus emergencyStatus = EmergencyStatus.NONE;
        int timeAddedToSim = 50;

        // Act
        Aircraft a = new Aircraft(
                callSign, origin, destination,
                scheduledTime, altitude, groundSpeed,
                initialFuel, emergencyStatus, timeAddedToSim
        );

        // Assert (all getters)
        assertAll(
                () -> assertEquals(callSign, a.getCallSign()),
                () -> assertEquals(origin, a.getOrigin()),
                () -> assertEquals(destination, a.getDestination()),
                () -> assertEquals(scheduledTime, a.getScheduledTime(), 1e-9),
                () -> assertEquals(altitude, a.getAltitude()),
                () -> assertEquals(groundSpeed, a.getGroundSpeed()),
                () -> assertEquals(emergencyStatus, a.getEmergencyStatus()),
                () -> assertEquals(initialFuel, a.getInitialFuel(), 1e-9)
        );
    }

    @Test
    void getFuelRemaining_shouldReturnInitialFuelAtTimeAdded() {
        Aircraft a = new Aircraft(
                "CS1",  "AAA", "BBB",
                0, 0, 0,
                100, EmergencyStatus.NONE, 10
        );

        // simTime == timeAddedToSim => fuelRemaining == initialFuel
        assertEquals(100.0, a.getFuelRemaining(10), 1e-9);
    }

    @Test
    void getFuelRemaining_shouldDecreaseLinearlyWithTime() {
        // initialFuel=100, burn=2 per unit time, added at t=10
        Aircraft a = new Aircraft(
                "CS2", "AAA", "BBB",
                0, 0, 0,
                100,  EmergencyStatus.NONE, 10
        );

        // at t=15 => 100 - 1*(15-10) = 95
        assertEquals(95.0, a.getFuelRemaining(15));

        // at t=25 => 100 - 1*(25-10) = 85
        assertEquals(85.0, a.getFuelRemaining(25));
    }

    @Test
    void isFuelCritical_shouldBeFalseWhenFuelIsExactly10() {
        // fuel critical condition is: getFuelRemaining(simTime) < 10
        Aircraft a = new Aircraft(
                "CS3",  "AAA", "BBB",
                0, 0, 0,
                20, EmergencyStatus.NONE, 0
        );

        // at t=10 => 20 - 1*(10-0)=10 => NOT critical (strictly < 10)
        assertFalse(a.isFuelCritical(10, 10));
        assertEquals(10.0, a.getFuelRemaining(10), 1e-9);
    }

    @Test
    void isFuelCritical_shouldBeTrueWhenFuelDropsBelow10() {
        Aircraft a = new Aircraft(
                "CS4",  "AAA", "BBB",
                0, 0, 0,
                20, EmergencyStatus.NONE, 0
        );

        // at t=10.1 => fuel = 9.9 => critical
        assertTrue(a.isFuelCritical(11, 10));
        assertTrue(a.getFuelRemaining(11) < 10.0);
    }
}
