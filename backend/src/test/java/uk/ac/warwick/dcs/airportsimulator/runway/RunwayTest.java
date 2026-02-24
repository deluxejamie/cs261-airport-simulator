package uk.ac.warwick.dcs.airportsimulator.runway;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;

import static org.junit.jupiter.api.Assertions.*;

class RunwayTest {

    @Test
    void constructor_shouldSetAllAttributesCorrectly() {
        // Arrange
        int runwayNumber = 1;
        double length = 3500.0;
        double bearing = 90.0;
        RunwayMode mode = RunwayMode.LANDING;
        RunwayStatus status = RunwayStatus.AVAILABLE;
        Aircraft occupied = new Aircraft();

        // Act
        Runway runway = new Runway(runwayNumber, length, bearing, mode, status, occupied);

        // Assert
        assertEquals(runwayNumber, runway.getRunwayNumber());
        assertEquals(length, runway.getLength(), 1e-9);
        assertEquals(bearing, runway.getBearing(), 1e-9);
        assertEquals(mode, runway.getMode());
        assertEquals(status, runway.getStatus());
        assertSame(occupied, runway.getOccupied());
    }

    @Test
    void setMode_shouldUpdateMode() {
        Runway runway = new Runway(1, 3000.0, 180.0,
                RunwayMode.LANDING, RunwayStatus.AVAILABLE, null);

        runway.setMode(RunwayMode.TAKE_OFF);

        assertEquals(RunwayMode.TAKE_OFF, runway.getMode());
    }

    @Test
    void setStatus_shouldUpdateStatus() {
        Runway runway = new Runway(2, 2800.0, 45.0,
                RunwayMode.MIXED_MODE, RunwayStatus.AVAILABLE, null);

        runway.setStatus(RunwayStatus.EQUIPMENT_FAILURE);

        assertEquals(RunwayStatus.EQUIPMENT_FAILURE, runway.getStatus());
    }

    @Test
    void setOccupied_shouldUpdateOccupiedAircraft() {
        Runway runway = new Runway(3, 2500.0, 270.0,
                RunwayMode.TAKE_OFF, RunwayStatus.AVAILABLE, null);

        Aircraft a1 = new Aircraft();
        Aircraft a2 = new Aircraft();

        runway.setOccupied(a1);
        assertSame(a1, runway.getOccupied());

        runway.setOccupied(a2);
        assertSame(a2, runway.getOccupied());

        runway.setOccupied(null);
        assertNull(runway.getOccupied());
    }

    @Test
    void setters_shouldAllowPuttingObjectIntoDifferentStates() {
        Runway runway = new Runway(4, 4000.0, 135.0,
                RunwayMode.LANDING, RunwayStatus.AVAILABLE, null);

        runway.setMode(RunwayMode.MIXED_MODE);
        runway.setStatus(RunwayStatus.RUNWAY_INSPECTION);
        runway.setOccupied(new Aircraft());

        assertAll(
                () -> assertEquals(RunwayMode.MIXED_MODE, runway.getMode()),
                () -> assertEquals(RunwayStatus.RUNWAY_INSPECTION, runway.getStatus()),
                () -> assertNotNull(runway.getOccupied())
        );
    }
}
