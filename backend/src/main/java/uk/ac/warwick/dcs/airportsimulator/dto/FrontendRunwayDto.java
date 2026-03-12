package uk.ac.warwick.dcs.airportsimulator.dto;

/**
 * Data transfer object representing a runway submitted by the frontend.
 *
 * This DTO contains the runway identifier and its operating mode.
 */
public class FrontendRunwayDto {
    private Integer id;
    private String mode;

    /**
     * Returns the runway identifier.
     *
     * @return runway id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the runway identifier.
     *
     * @param id runway id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the runway operating mode.
     *
     * @return runway mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the runway operating mode.
     *
     * @param mode runway mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
}