package uk.ac.warwick.dcs.airportsimulator.dto;

public class FrontendRunwayDto {
    private Integer id;
    private String mode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}