package uk.ac.warwick.dcs.airportsimulator.priorityqueue;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;

import java.util.ArrayDeque;
import java.util.Queue;
/**
 * FIFO queue for aircraft waiting to take off.
 */
public final class TakeOffQueue {

    /**
     * Constructs a new take off queue
     */
    private final Queue<Aircraft> queue = new ArrayDeque<>();

    /**
     * @return the size of the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Adds an aircraft to the queue
     * @param a aircraft
     */
    public void addAircraft(Aircraft a) {
        queue.add(a);
    }

    /**
     * Peeks next aircraft
     * @return the next aircraft
     */
    public Aircraft peekNextAircraft() {
        return queue.peek();
    }

    /**
     * Returns next aircraft and removes
     * @return the next aircraft
     */
    public Aircraft getNextAircraft() {
        return queue.poll();
    }
}