package uk.ac.warwick.dcs.airportsimulator.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;

import java.nio.file.Files;

@JsonTest
public class BaseServiceTest {

    @Autowired
    protected JacksonTester<SimulationRequestDto> json;

    protected String JSON_FROM_FILE;

    @BeforeEach
    public void setup() throws Exception {
        JSON_FROM_FILE = Files.readString(new ClassPathResource("flight_configuration.json").getFile().toPath());
    }
}
