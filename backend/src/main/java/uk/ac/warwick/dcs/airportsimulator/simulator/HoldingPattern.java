package uk.ac.warwick.dcs.airportsimulator.simulator;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.aircraft.EmergencyStatus;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Queue for aircraft waiting to land (holding pattern).
 *
 * Priority:
 * 1) Emergency aircraft first
 * 2) Then lower initial fuel first (more urgent)
 * 3) Then FIFO
 */
public final class HoldingPattern {

    private static final class Entry {
        final Aircraft aircraft;
        final long seq;

        Entry(Aircraft aircraft, long seq) {
            this.aircraft = aircraft;
            this.seq = seq;
        }
    }

    private final PriorityQueue<Entry> queue;
    private final AtomicLong seq = new AtomicLong(0);

    /** Max aircraft allowed in holding pattern. <=0 means unlimited. */
    private final int capacity;

    public HoldingPattern(int capacity) {
        this.capacity = capacity;

        this.queue = new PriorityQueue<>((e1, e2) -> {
            Aircraft a = e1.aircraft;
            Aircraft b = e2.aircraft;

            boolean aEmer = a.getEmergencyStatus() != EmergencyStatus.NONE;
            boolean bEmer = b.getEmergencyStatus() != EmergencyStatus.NONE;
            if (aEmer != bEmer) return aEmer ? -1 : 1;

            // Lower initial fuel => higher urgency
            int fuelCmp = Integer.compare(a.getInitialFuel(), b.getInitialFuel());
            if (fuelCmp != 0) return fuelCmp;

            // FIFO tie-breaker
            return Long.compare(e1.seq, e2.seq);
        });
    }

    public int size() {
        return queue.size();
    }

    public boolean isFull() {
        return capacity > 0 && queue.size() >= capacity;
    }

    public boolean addAircraft(Aircraft a) {
        if (isFull()) return false;
        queue.add(new Entry(a, seq.getAndIncrement()));
        return true;
    }

    public Aircraft peekNextAircraft() {
        Entry e = queue.peek();
        return e == null ? null : e.aircraft;
    }

    public Aircraft getNextAircraft() {
        Entry e = queue.poll();
        return e == null ? null : e.aircraft;
    }

    /** Optional helper for diversion checks in Simulation loop */
    public Aircraft pollIfFuelCritical(int simTime) {
        Entry e = queue.peek();
        if (e == null) return null;
        if (e.aircraft.isFuelCritical(simTime)) {
            queue.poll();
            return e.aircraft;
        }
        return null;
    }
}