package uk.ac.warwick.dcs.airportsimulator.runway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RunwayModeTest {

    @Test
    void runwayMode_shouldContainExpectedConstants() {
        assertNotNull(RunwayMode.valueOf("LANDING"));
        assertNotNull(RunwayMode.valueOf("TAKE_OFF"));
        assertNotNull(RunwayMode.valueOf("MIXED_MODE"));
    }

    @Test
    void runwayMode_valuesOrder_shouldMatchDeclaration() {
        RunwayMode[] values = RunwayMode.values();
        assertArrayEquals(
                new RunwayMode[]{RunwayMode.LANDING, RunwayMode.TAKE_OFF, RunwayMode.MIXED_MODE},
                values
        );
    }
}
