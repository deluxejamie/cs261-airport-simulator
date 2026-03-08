package uk.ac.warwick.dcs.airportsimulator.dto;

import java.util.ArrayList;
import java.util.List;

public class SimulationRequestDto {
    private List<FrontendRunwayDto> runways = new ArrayList<>();
    private AdvancedConfigDto advancedConfig;
    private List<FrontendFlightDto> flights = new ArrayList<>();
    private List<FrontendHazardDto> hazards = new ArrayList<>();

    public List<FrontendRunwayDto> getRunways() {
        return runways;
    }

    public void setRunways(List<FrontendRunwayDto> runways) {
        this.runways = runways;
    }

    public AdvancedConfigDto getAdvancedConfig() {
        return advancedConfig;
    }

    public void setAdvancedConfig(AdvancedConfigDto advancedConfig) {
        this.advancedConfig = advancedConfig;
    }

    public List<FrontendFlightDto> getFlights() {
        return flights;
    }

    public void setFlights(List<FrontendFlightDto> flights) {
        this.flights = flights;
    }

    public List<FrontendHazardDto> getHazards() {
        return hazards;
    }

    public void setHazards(List<FrontendHazardDto> hazards) {
        this.hazards = hazards;
    }
}