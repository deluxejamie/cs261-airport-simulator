package uk.ac.warwick.dcs.airportsimulator.runway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RunwayStatusTest {

    @Test
    void runwayStatus_shouldContainExpectedConstants() {
        assertNotNull(RunwayStatus.valueOf("AVAILABLE"));
        assertNotNull(RunwayStatus.valueOf("RUNWAY_INSPECTION"));
        assertNotNull(RunwayStatus.valueOf("SNOW_CLEARANCE"));
        assertNotNull(RunwayStatus.valueOf("EQUIPMENT_FAILURE"));
    }

    @Test
    void runwayStatus_valuesOrder_shouldMatchDeclaration() {
        assertArrayEquals(
                new RunwayStatus[]{
                        RunwayStatus.AVAILABLE,
                        RunwayStatus.RUNWAY_INSPECTION,
                        RunwayStatus.SNOW_CLEARANCE,
                        RunwayStatus.EQUIPMENT_FAILURE
                },
                RunwayStatus.values()
        );
    }
}
