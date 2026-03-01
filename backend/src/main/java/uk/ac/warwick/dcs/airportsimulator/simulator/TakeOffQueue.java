package uk.ac.warwick.dcs.airportsimulator.simulator;

import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * FIFO queue for aircraft waiting to take off.
 */
public final class TakeOffQueue {

    private final Queue<Aircraft> queue = new ArrayDeque<>();

    public int size() {
        return queue.size();
    }

    public void addAircraft(Aircraft a) {
        queue.add(a);
    }

    public Aircraft peekNextAircraft() {
        return queue.peek();
    }

    public Aircraft getNextAircraft() {
        return queue.poll();
    }
}