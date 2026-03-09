package uk.ac.warwick.dcs.airportsimulator.dto;

/**
 * Data transfer object representing repeating schedule information.
 *
 * This DTO defines how long a repeated event lasts and how frequently it repeats.
 */
public class RepeatingDto {
    private Integer end;
    private Integer period;

    /**
     * Returns the end time of the repeating schedule.
     *
     * @return end time
     */
    public Integer getEnd() {
        return end;
    }

    /**
     * Sets the end time of the repeating schedule.
     *
     * @param end end time
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     * Returns the repetition period.
     *
     * @return repetition period
     */
    public Integer getPeriod() {
        return period;
    }

    /**
     * Sets the repetition period.
     *
     * @param period repetition period
     */
    public void setPeriod(Integer period) {
        this.period = period;
    }
}