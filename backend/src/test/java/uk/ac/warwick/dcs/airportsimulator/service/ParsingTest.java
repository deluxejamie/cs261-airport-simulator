package uk.ac.warwick.dcs.airportsimulator.service;


import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import uk.ac.warwick.dcs.airportsimulator.dto.FrontendRunwayDto;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;
import uk.ac.warwick.dcs.airportsimulator.runway.Runway;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ParsingTest {

    @Autowired
    private JacksonTester<SimulationRequestDto> json;

    private String JSON_FROM_FILE;

    @BeforeEach
    public void setup() throws Exception {
        JSON_FROM_FILE = Files.readString(new ClassPathResource("flight_configuration.json").getFile().toPath());
    }



    @Test
    void testRunways() throws Exception {
        final List<FrontendRunwayDto> runways = json.parseObject(JSON_FROM_FILE).getRunways();
        assertEquals(4, runways.size());

        final String[] expected = {"mixed_mode", "takeoff", "landing", "mixed_mode"};
        for (int i = 0; i < 4; ++i)
        {
            assertEquals(runways.get(i).getId(), i + 1);
            assertEquals(runways.get(i).getType(), expected[i]);
        }
    }



}
