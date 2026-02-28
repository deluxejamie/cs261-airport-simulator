package uk.ac.warwick.dcs.airportsimulator.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmergencyStatusTest {

    @Test
    void emergencyStatus_shouldContainExpectedConstants() {
        assertNotNull(EmergencyStatus.valueOf("NONE"));
        assertNotNull(EmergencyStatus.valueOf("FUEL"));
        assertNotNull(EmergencyStatus.valueOf("MECH_FAIL"));
        assertNotNull(EmergencyStatus.valueOf("PASSENGER_HEALTH"));
    }

    @Test
    void emergencyStatus_valuesOrder_shouldMatchDeclaration() {
        assertArrayEquals(
                new EmergencyStatus[]{
                        EmergencyStatus.NONE,
                        EmergencyStatus.FUEL,
                        EmergencyStatus.MECH_FAIL,
                        EmergencyStatus.PASSENGER_HEALTH
                },
                EmergencyStatus.values()
        );
    }
}
