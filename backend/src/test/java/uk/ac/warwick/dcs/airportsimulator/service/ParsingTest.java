package uk.ac.warwick.dcs.airportsimulator.service;


import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.airportsimulator.dto.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParsingTest extends BaseServiceTest {

    @Test
    void testRunways() throws Exception {
        final List<FrontendRunwayDto> runways = json.parseObject(JSON_FROM_FILE).getRunways();
        assertEquals(4, runways.size());

        final String[] expected = {"mixed_mode", "takeoff", "landing", "mixed_mode"};
        for (int i = 0; i < 4; ++i)
        {
            assertEquals(runways.get(i).getId(), i + 1);
            assertEquals(runways.get(i).getMode(), expected[i]);
        }
    }

    @Test
    void testAdvancedConfig() throws Exception
    {
        final AdvancedConfigDto advancedConfig = json.parseObject(JSON_FROM_FILE).getAdvancedConfig();

        assertEquals(17, advancedConfig.getMaxDelayBeforeCancelled());
        assertEquals(15, advancedConfig.getFuelThresholdBeforeRedirected());
        assertEquals(8, advancedConfig.getTimeTakenForTakeoff());
        assertEquals(9, advancedConfig.getTimeTakenForLanding());
    }

    @Test
    void testFlights() throws Exception
    {
        final List<FrontendFlightDto> flights = json.parseObject(JSON_FROM_FILE).getFlights();
        assertEquals(7, flights.size());

        {
            final FrontendFlightDto easyjet = flights.getFirst();
            assertEquals("EASYJET-1", easyjet.getCallsign());
            assertEquals(0, easyjet.getExpectedDepartureTime());
            assertEquals(24880, easyjet.getSeed());
            assertEquals(60, easyjet.getRepeating().getEnd());
            assertEquals(10, easyjet.getRepeating().getPeriod());
            assertEquals(1, easyjet.getId());
            assertEquals("departure", easyjet.getType());
        }

        {
            final FrontendFlightDto ryanair = flights.get(1);
            assertEquals("RYANAIR-2", ryanair.getCallsign());
            assertEquals(50, ryanair.getExpectedDepartureTime());
            assertEquals(90712, ryanair.getSeed());
            assertEquals(1000, ryanair.getRepeating().getEnd());
            assertEquals(10, ryanair.getRepeating().getPeriod());
            assertEquals(2, ryanair.getId());
            assertEquals("departure", ryanair.getType());
        }

        {
            final FrontendFlightDto ba = flights.get(2);
            assertEquals("BA-3", ba.getCallsign());
            assertEquals(68, ba.getExpectedArrivalTime());
            assertEquals("none", ba.getEmergencyStatus());
            assertEquals(20, ba.getRemainingFuelMins());
            assertEquals(10866, ba.getSeed());
            assertEquals(3, ba.getId());
            assertEquals("arrival", ba.getType());
        }

        {
            final FrontendFlightDto nw = flights.get(3);
            assertEquals("NW-4", nw.getCallsign());
            assertEquals(65, nw.getExpectedArrivalTime());
            assertEquals("mech_fail", nw.getEmergencyStatus());
            assertEquals(16, nw.getRemainingFuelMins());
            assertEquals(1735, nw.getSeed());
            assertEquals(4, nw.getId());
            assertEquals("arrival", nw.getType());
        }

        {
            final FrontendFlightDto evaAir = flights.get(4);
            assertEquals("EVA AIR-5", evaAir.getCallsign());
            assertEquals(879, evaAir.getExpectedArrivalTime());
            assertEquals("passenger_health", evaAir.getEmergencyStatus());
            assertEquals(78, evaAir.getRemainingFuelMins());
            assertEquals(54053, evaAir.getSeed());
            assertEquals(5, evaAir.getId());
            assertEquals("arrival", evaAir.getType());
        }

        {
            final FrontendFlightDto cathy = flights.get(5);
            assertEquals("CATHY-6", cathy.getCallsign());
            assertEquals(6575, cathy.getExpectedArrivalTime());
            assertEquals("none", cathy.getEmergencyStatus());
            assertEquals(65, cathy.getRemainingFuelMins());
            assertEquals(89027, cathy.getSeed());
            assertEquals(13421, cathy.getRepeating().getEnd());
            assertEquals(23, cathy.getRepeating().getPeriod());
            assertEquals(6, cathy.getId());
            assertEquals("arrival", cathy.getType());
        }

        {
            final FrontendFlightDto virgin = flights.get(6);
            assertEquals("VIRGIN-7", virgin.getCallsign());
            assertEquals(23, virgin.getExpectedArrivalTime());
            assertEquals("passenger_health", virgin.getEmergencyStatus());
            assertEquals(54, virgin.getRemainingFuelMins());
            assertEquals(18875, virgin.getSeed());
            assertEquals(6757, virgin.getRepeating().getEnd());
            assertEquals(65, virgin.getRepeating().getPeriod());
            assertEquals(7, virgin.getId());
            assertEquals("arrival", virgin.getType());
        }
    }


    @Test
    void testHazards() throws Exception
    {
        final List<FrontendHazardDto> hazards = json.parseObject(JSON_FROM_FILE).getHazards();
        assertEquals(5, hazards.size());

        {
            final FrontendHazardDto h = hazards.getFirst();
            assertEquals(1, h.getId());
            assertEquals("runway_closure", h.getType());
            assertEquals(34, h.getTime());
            assertEquals(15, h.getDurationMins());
            assertEquals(1, h.getAffectedRunway());
            assertEquals("snow_clearance", h.getClosureMode());
        }

        {
            final FrontendHazardDto h = hazards.get(1);
            assertEquals(2, h.getId());
            assertEquals("runway_closure", h.getType());
            assertEquals(56, h.getTime());
            assertEquals(34, h.getDurationMins());
            assertEquals(3, h.getAffectedRunway());
            assertEquals("runway_inspection", h.getClosureMode());
        }

        {
            final FrontendHazardDto h = hazards.get(2);
            assertEquals(3, h.getId());
            assertEquals("runway_closure", h.getType());
            assertEquals(34, h.getTime());
            assertEquals(54, h.getDurationMins());
            assertEquals(4, h.getAffectedRunway());
            assertEquals("equipment_failure", h.getClosureMode());
        }

        {
            final FrontendHazardDto h = hazards.get(3);
            assertEquals(4, h.getId());
            assertEquals("emergency_event", h.getType());
            assertEquals(879, h.getTime());
            assertEquals("EVA AIR-5", h.getTargetArrivalCallsign());
            assertEquals("passenger_health", h.getEmergencyType());
        }

        {
            final FrontendHazardDto h = hazards.get(4);
            assertEquals(5, h.getId());
            assertEquals("emergency_event", h.getType());
            assertEquals(65, h.getTime());
            assertEquals("NW-4", h.getTargetArrivalCallsign());
            assertEquals("mech_fail", h.getEmergencyType());
        }
    }
}
