package uk.ac.warwick.dcs.airportsimulator.events;

/**
 * Event interface implements events which can be recurring
 * or one shot
 */
public interface IEvent {

    /**
     * Returns if the event is a recurring event or not
     *
     * @return if event is recurring
     */
    public boolean isRecurring();

    /**
     * Executes the event
     */
    public void execute();

    /**
     * Gets the next scheduled time for the event
     *
     * @return the next scheduled time
     */
    public int getScheduledTime();

    /**
     * Generate the next event (if there is one)
     * Returns null if the current event is final one
     *
     * @return the next event (or null)
     */
    public IEvent next();
}
