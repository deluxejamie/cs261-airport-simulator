package uk.ac.warwick.dcs.airportsimulator.priorityqueue;

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

    /**
     * An Entry in the holding pattern
     * We use a seq to make it stable sorted.
     */
    private static final class Entry {
        final Aircraft aircraft;
        final long seq;

        Entry(Aircraft aircraft, long seq) {
            this.aircraft = aircraft;
            this.seq = seq;
        }
    }

    private final PriorityQueue<Entry> queue;
    private long seq = 0;

    /** Max aircraft allowed in holding pattern. <=0 means unlimited. */
    private final int capacity;

    /**
     * Constructs a Holding Pattern with given capacity
     * @param capacity the holding pattern capacity
     */
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

    /**
     * Gets the size of the holding pattern
     * @return the size of the holding pattern
     */
    public int size() {
        return queue.size();
    }

    /**
     * Returns if the holding pattern is full
     * @return if the holding pattern is full
     */
    public boolean isFull() {
        return capacity > 0 && queue.size() >= capacity;
    }

    /**
     * Adds an aircraft to the holding pattern
     * @param a the aircraft to add
     * @return  if we successfully added the aircraft
     */
    public boolean addAircraft(Aircraft a) {
        if (isFull()) return false;
        queue.add(new Entry(a, seq++));
        return true;
    }

    /**
     * Peaks the next aircraft in the queue
     * @return the next aircraft in the holding pattern
     */
    public Aircraft peekNextAircraft() {
        Entry e = queue.peek();
        return e == null ? null : e.aircraft;
    }

    /**
     * Gets the next aircraft in the queue,
     *     removing it from the queue
     * @return the next aircraft in the queue
     */
    public Aircraft getNextAircraft() {
        Entry e = queue.poll();
        return e == null ? null : e.aircraft;
    }

    /**
     * Gets the next aircraft if the fuel is critical
     * @param simTime the sim time
     * @return        the next aircraft if fuel is critical otherwise null
     */
    public Aircraft pollIfFuelCritical(int simTime) {
        Entry e = queue.peek();
        if (e == null) return null;
        if (e.aircraft.isFuelCritical(simTime)) {
            queue.poll();
            return e.aircraft;
        }
        return null;
    }

    /**
     * Whether the holding pattern contains a specified aircraft
     * @param a the aircraft
     * @return  whether the aircraft is in the holding pattern
     */
    public boolean containsAircraft(Aircraft a) {
        for (Entry e : queue) {
            if (e.aircraft == a) return true;
        }
        return false;
    }

    /**
     * Removes an aircraft from the holding pattern
     * @param a the aircraft to remove
     * @return  if the aircraft was in the holding pattern and removed
     */
    public boolean removeAircraft(Aircraft a) {
        Entry found = null;
        for (Entry e : queue) {
            if (e.aircraft == a) {
                found = e;
                break;
            }
        }
        if (found == null) return false;
        return queue.remove(found);
    }
}
