package uk.ac.warwick.dcs.airportsimulator.eventlog;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventLogTest {

    private static EventLogEntry entry(EventType type, int ts) {
        return new EventLogEntry(type, ts, new HashMap<>());
    }

    @Test
    void newEventLog_shouldReturnEmptyEvents() {
        EventLog log = new EventLog(); // package-private ctor
        List<EventLogEntry> events = log.getEvents(0, 10);

        assertTrue(events.isEmpty());
    }

    @Test
    void addEntry_shouldIncreaseReturnedEvents() {
        EventLog log = new EventLog();

        EventLogEntry e1 = entry(EventType.LANDING_EVENT, 1);
        EventLogEntry e2 = entry(EventType.TAKEOFF_EVENT, 2);

        log.addEntry(e1);
        log.addEntry(e2);

        List<EventLogEntry> events = log.getEvents(0, 10);

        assertEquals(2, events.size());
        assertSame(e1, events.get(0));
        assertSame(e2, events.get(1));
    }

    @Test
    void getEvents_shouldRespectOffsetAndCount_andClampToSize() {
        EventLog log = new EventLog();
        EventLogEntry e1 = entry(EventType.LANDING_EVENT, 1);
        EventLogEntry e2 = entry(EventType.TAKEOFF_EVENT, 2);
        EventLogEntry e3 = entry(EventType.HOLDING_EVENT, 3);

        log.addEntry(e1);
        log.addEntry(e2);
        log.addEntry(e3);

        // offset=1, count=1 -> only e2
        List<EventLogEntry> slice1 = log.getEvents(1, 1);
        assertEquals(1, slice1.size());
        assertSame(e2, slice1.get(0));

        // offset=1, count=10 -> should clamp to end -> e2, e3
        List<EventLogEntry> slice2 = log.getEvents(1, 10);
        assertEquals(2, slice2.size());
        assertSame(e2, slice2.get(0));
        assertSame(e3, slice2.get(1));
    }

    @Test
    void getEvents_returnsView_modifyingReturnedListModifiesUnderlyingLog() {
        EventLog log = new EventLog();
        EventLogEntry e1 = entry(EventType.LANDING_EVENT, 1);
        EventLogEntry e2 = entry(EventType.TAKEOFF_EVENT, 2);
        EventLogEntry e3 = entry(EventType.HOLDING_EVENT, 3);

        log.addEntry(e1);
        log.addEntry(e2);
        log.addEntry(e3);

        List<EventLogEntry> view = log.getEvents(0, 2); // [e1, e2]
        view.remove(0); // removes e1 from underlying list as well

        List<EventLogEntry> allNow = log.getEvents(0, 10);
        assertEquals(2, allNow.size());
        assertSame(e2, allNow.get(0));
        assertSame(e3, allNow.get(1));
    }
}
