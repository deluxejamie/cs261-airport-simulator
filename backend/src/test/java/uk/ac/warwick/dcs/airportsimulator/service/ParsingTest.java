package uk.ac.warwick.dcs.airportsimulator.service;


import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import uk.ac.warwick.dcs.airportsimulator.dto.SimulationRequestDto;

import java.io.IOException;
import java.nio.file.Files;
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
    void somethingWorks() throws Exception {
        json.parseObject(JSON_FROM_FILE);
    }



}
