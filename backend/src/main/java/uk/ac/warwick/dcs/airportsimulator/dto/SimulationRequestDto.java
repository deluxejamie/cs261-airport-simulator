package uk.ac.warwick.dcs.airportsimulator.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level data transfer object representing a simulation request
 * received from the frontend.
 *
 * This DTO groups runway definitions, advanced configuration settings,
 * flight definitions, and hazard definitions into a single request body.
 */
public class SimulationRequestDto {
    private List<FrontendRunwayDto> runways = new ArrayList<>();
    private AdvancedConfigDto advancedConfig;
    private List<FrontendFlightDto> flights = new ArrayList<>();
    private List<FrontendHazardDto> hazards = new ArrayList<>();

    /**
     * Returns the list of configured runways.
     *
     * @return runway list
     */
    public List<FrontendRunwayDto> getRunways() {
        return runways;
    }

    /**
     * Sets the list of configured runways.
     *
     * @param runways runway list
     */
    public void setRunways(List<FrontendRunwayDto> runways) {
        this.runways = runways;
    }

    /**
     * Returns the advanced simulation configuration.
     *
     * @return advanced configuration
     */
    public AdvancedConfigDto getAdvancedConfig() {
        return advancedConfig;
    }

    /**
     * Sets the advanced simulation configuration.
     *
     * @param advancedConfig advanced configuration
     */
    public void setAdvancedConfig(AdvancedConfigDto advancedConfig) {
        this.advancedConfig = advancedConfig;
    }

    /**
     * Returns the list of flight definitions.
     *
     * @return flight list
     */
    public List<FrontendFlightDto> getFlights() {
        return flights;
    }

    /**
     * Sets the list of flight definitions.
     *
     * @param flights flight list
     */
    public void setFlights(List<FrontendFlightDto> flights) {
        this.flights = flights;
    }

    /**
     * Returns the list of hazard definitions.
     *
     * @return hazard list
     */
    public List<FrontendHazardDto> getHazards() {
        return hazards;
    }

    /**
     * Sets the list of hazard definitions.
     *
     * @param hazards hazard list
     */
    public void setHazards(List<FrontendHazardDto> hazards) {
        this.hazards = hazards;
    }
}