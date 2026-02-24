package uk.ac.warwick.dcs.airportsimulator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationEntity;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationResultEntity;
import uk.ac.warwick.dcs.airportsimulator.service.SimulationService;
import uk.ac.warwick.dcs.airportsimulator.simulator.SimulationResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataBaseTest
{
    /* Get access to db in tests */
    @Autowired
    private SimulationService simulationService;

    /**
     * Tests EventLogEntity
     */
    @Test
    public void testEventLogEntity()
    {
        final String uuid = UUID.randomUUID().toString();
        final String eventType = randomString();
        final double timestamp = randomDouble();
        final String attr = randomString();

        final EventLogEntryEntity e = new EventLogEntryEntity(uuid, eventType, timestamp, attr);

        assertEquals(e.getSimulationId(), uuid);
        assertEquals(e.getEventType(), eventType);
        assertEquals(e.getSimTimestamp(), timestamp);
        assertEquals(e.getAttributes(), attr);
    }

    /**
     * Tests SimulationEntity
     */
    @Test
    public void testSimulationEntity()
    {
        final String id = UUID.randomUUID().toString();
        final String status = randomString();
        final String config = randomString();

        final LocalDateTime timeNow = LocalDateTime.now();

        final SimulationEntity se = new SimulationEntity(id, status, config);

        assertEquals(se.getId(), id);
        assertEquals(se.getStatus(), status);
        assertEquals(se.getConfig(), config);

        assertTrue(Duration.between(timeNow, se.getCreatedAt()).getSeconds() <= 60, "Difference between creation and constructor initialisation is less than 60 seconds");
    }

    /**
     * Tests SimulationResultEntity
     */
    @Test
    public void testSimulationResultEntity()
    {
        final String id = UUID.randomUUID().toString();
        final int maxTakeOffQueue = randomInt();
        final double avgTakeOffWait = randomInt();
        final int maxHoldQueue = randomInt();
        final double avgHoldTime = randomDouble();
        final int totalCancellations = randomInt();
        final int totalDiversion = randomInt();
        final double avgArrivalDelay = randomDouble();
        final double avgDepartureDelay = randomDouble();

        final SimulationResultEntity sre = new SimulationResultEntity(id, maxTakeOffQueue, avgTakeOffWait, maxHoldQueue, avgHoldTime, totalCancellations, totalDiversion, avgArrivalDelay, avgDepartureDelay);

        assertEquals(sre.getSimulationId(), id);
        assertEquals(sre.getMaxTakeOffQueue(), maxTakeOffQueue);
        assertEquals(sre.getAvgTakeOffWait(), avgTakeOffWait);
        assertEquals(sre.getMaxHoldQueue(), maxHoldQueue);
        assertEquals(sre.getAvgHoldTime(), avgHoldTime);
        assertEquals(sre.getTotalCancellations(), totalCancellations);
        assertEquals(sre.getTotalDiversions(), totalDiversion);
        assertEquals(sre.getAvgArrivalDelay(), avgArrivalDelay);
        assertEquals(sre.getAvgDepartureDelay(), avgDepartureDelay);
    }

    /**
     * Tests saving event logs
     */
    @Test
    public void testSavingEventLogEntries()
    {
        final int TEST_ITERATIONS = 1000;
        List<EventLogEntryEntity> entries = new ArrayList<>();

        final String uuid = createInitialSim();

        double timestamp = 0;

        /* Create random test data */
        for (int i = 0; i < TEST_ITERATIONS; ++i)
        {
            final String eventType = randomString();
            timestamp += randomDouble();
            final String attr = randomString();

            final EventLogEntryEntity e = new EventLogEntryEntity(uuid, eventType, timestamp, attr);
            entries.add(e);
        }

        /* Insert test data */
        for (int i = 0; i < TEST_ITERATIONS; ++i)
        {
            final EventLogEntryEntity e = entries.get(i);
            simulationService.saveEventLogEntry(uuid, e.getEventType(), e.getSimTimestamp(), e.getAttributes());
        }

        /* Test matches */

        List<EventLogEntryEntity> eventLog = simulationService.getEventLog(uuid, 0, TEST_ITERATIONS);
        assertEquals(TEST_ITERATIONS, eventLog.size());

        for (int i = 0; i < TEST_ITERATIONS; ++i)
        {
            final EventLogEntryEntity lhs = eventLog.get(i);
            final EventLogEntryEntity rhs = entries.get(i);
            assertEquals(lhs.getSimulationId(), rhs.getSimulationId());
            assertEquals(lhs.getEventType(), rhs.getEventType());
            assertEquals(lhs.getSimTimestamp(), rhs.getSimTimestamp());
            assertEquals(lhs.getAttributes(), rhs.getAttributes());
        }
    }

    /**
     * Checks status
     */
    @Test
    public void checkStatus() {
        {
            final String uuid = randomString();
            assertTrue(simulationService.getStatus(uuid).isEmpty());
        }

        {
            final String uuid = createInitialSim();
            assertEquals("RUNNING", simulationService.getStatus(uuid).orElseThrow());
        }

        {
            final String uuid = createInitialSim();
            simulationService.saveResult(uuid, new SimulationResult());
            assertEquals("COMPLETED", simulationService.getStatus(uuid).orElseThrow());
        }
    }

    /**
     * Test fetching results from database
     */
    @Test
    public void testFetchingResult()
    {
        final String uuid = createInitialSim();
        final int maxTakeOffQueue = randomInt();
        final int maxHoldQueue = randomInt();
        final int totalCancellations = randomInt();
        final int totalDiversion = randomInt();
        final double avgTakeOffWait = randomInt();
        final double avgHoldTime = randomDouble();
        final double avgArrivalDelay = randomDouble();
        final double avgDepartureDelay = randomDouble();

        SimulationResult sr = new SimulationResult();
        sr.recordTakeoffQueueSize(maxTakeOffQueue, 0);
        sr.recordHoldQueueSize(maxHoldQueue, 0);

        for (int i = 0; i < totalCancellations; ++i) sr.recordCancellation();
        for (int i = 0; i < totalDiversion; ++i) sr.recordDiversion();

        sr.recordTakeoffWait(avgTakeOffWait);
        sr.recordHoldTime(avgHoldTime);
        sr.recordArrivalDelay(avgArrivalDelay);
        sr.recordDepartureDelay(avgDepartureDelay);

        simulationService.saveResult(uuid, sr);

        final SimulationResultEntity sre = simulationService.getResult(uuid).orElseThrow();

        assertEquals(sre.getMaxTakeOffQueue(), maxTakeOffQueue);
        assertEquals(sre.getMaxHoldQueue(), maxHoldQueue);
        assertEquals(sre.getTotalCancellations(), totalCancellations);
        assertEquals(sre.getTotalDiversions(), totalDiversion);
        assertEquals(sre.getAvgTakeOffWait(), avgTakeOffWait);
        assertEquals(sre.getAvgHoldTime(), avgHoldTime);
        assertEquals(sre.getAvgArrivalDelay(), avgArrivalDelay);
        assertEquals(sre.getAvgDepartureDelay(), avgDepartureDelay);
    }



    /**
     * Creates initial sim without configJson
     * @return sim uuid
     */
    private String createInitialSim()
    {
        return createInitialSim("{}");
    }

    /**
     * Creates initial sim
     * @param configJson sim config json
     * @return sim uuid
     */
    private String createInitialSim(final String configJson)
    {
        final String uuid = UUID.randomUUID().toString();
        simulationService.createSimulation(uuid, configJson);
        return uuid;
    }

    /**
     * Generate random string using uuid class
     * @return random string
     */
    private String randomString() { return UUID.randomUUID().toString(); }

    /**
     * Generate random string using uuid class
     * @return random string
     */
    private int randomInt() { return (int) (Math.random() * MAX_RAND); }

    /**
     * Generate random double using uuid class
     * @return random double
     */
    private double randomDouble() { return Math.random() * MAX_RAND; }

    private static final int MAX_RAND = 1000;
}
