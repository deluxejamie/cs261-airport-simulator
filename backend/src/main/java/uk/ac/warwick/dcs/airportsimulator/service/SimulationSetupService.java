package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.stereotype.Service;
import uk.ac.warwick.dcs.airportsimulator.aircraft.Aircraft;
import uk.ac.warwick.dcs.airportsimulator.runway.RunwayStatus;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;

import java.util.HashMap;
import java.util.Map;

@Service
public class SimulationSetupService {

    public Simulation setupSimulation(ParsedSimulationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("ParsedSimulationConfig must not be null.");
        }

        Simulation simulation = new Simulation(config.getRunways());

        simulation.setMaxDelayBeforeCancelled(config.getMaxDelayBeforeCancelled());
        simulation.setFuelThresholdBeforeRedirected(config.getFuelThresholdBeforeRedirected());
        simulation.setTimeTakenForTakeoff(config.getTimeTakenForTakeoff());
        simulation.setTimeTakenForLanding(config.getTimeTakenForLanding());
        
        Map<String, Aircraft> aircraftByCallsign = new HashMap<>();

        for (ParsedSimulationConfig.ParsedFlight flight : config.getFlights()) {
            Aircraft aircraft = flight.getAircraft();
            aircraftByCallsign.put(aircraft.getCallSign(), aircraft);

            simulation.addAircraft(
                    aircraft,
                    flight.getScheduled(),
                    flight.getInterval(),
                    flight.getEnd(),
                    flight.getSeed(),
                    flight.getOp()
            );
        }

        for (ParsedSimulationConfig.ParsedRunwayClosure closure : config.getRunwayClosures()) {
            simulation.addRunwayStatusChange(
                    closure.getRunwayNumber(),
                    closure.getScheduled(),
                    closure.getInterval(),
                    closure.getEnd(),
                    closure.getStatus()
            );

            if (closure.getInterval() == 0) {
                simulation.addRunwayStatusChange(
                        closure.getRunwayNumber(),
                        closure.getEnd(),
                        0,
                        closure.getEnd(),
                        RunwayStatus.AVAILABLE
                );
            }
        }

        for (ParsedSimulationConfig.ParsedEmergencyEvent event : config.getEmergencyEvents()) {
            Aircraft aircraft = aircraftByCallsign.get(event.getCallsign());
            if (aircraft == null) {
                throw new IllegalStateException("Arrival aircraft not found: " + event.getCallsign());
            }

            simulation.addAircraftEmergency(
                    aircraft,
                    event.getScheduled(),
                    0,
                    event.getScheduled(),
                    event.getEmergencyStatus()
            );
        }

        return simulation;
    }
}