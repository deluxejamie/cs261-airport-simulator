package uk.ac.warwick.dcs.airportsimulator.service;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;
import uk.ac.warwick.dcs.airportsimulator.eventlog.EventLogEntry;
import uk.ac.warwick.dcs.airportsimulator.simulation.Simulation;
import uk.ac.warwick.dcs.airportsimulator.simulationresult.SimulationResult;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Handles running the simulations and scheduling
 */
@Service
public class SimManager {


    /**
     * Constructs a simamanger
     *
     * @param simulationService database service
     */
    public SimManager(SimulationService simulationService) {
        this.executor = Executors.newFixedThreadPool(nproc());
        this.simulationService = simulationService;
        this.simulationToState = new ConcurrentHashMap<>();
    }

    /**
     * Runs a simulation in thread pool and returns sim uuid
     *
     * @param simulation simulation to run
     * @param uuid       the uuid of the simulation
     */
    public void runSimulation(Simulation simulation, String uuid) {
        /* Sim already ran */
        if (simulationService.getResult(uuid).isPresent()) return;

        simulationToState.put(uuid, simulation);

        executor.execute(() -> {
            final SimulationResult result = simulation.run();
            simulationService.saveResult(uuid, result);
            simulationToState.remove(uuid);
        });
    }

    /**
     * Status of simulation
     *
     * @param uuid the uuid of the simulation
     * @return the simulation state if exists in memory
     */
    public Optional<String> getStatus(String uuid) {
        final Simulation sim = simulationToState.get(uuid);
        if (sim == null) return Optional.empty();
        return Optional.of("RUNNING");
    }

    /**
     * Returns the event log from simulation
     *
     * @param uuid   the uuid of the simulation
     * @param offset the offset to read from event log
     * @param count  the amount to read from the event log
     * @return the event log if the simulation exists in memory
     */
    public Optional<Tuple<List<EventLogEntry>, Long>> getEventLog(String uuid, int offset, int count) {
        final Simulation sim = simulationToState.get(uuid);
        if (sim == null) return Optional.empty();
        return withLock(sim.getMutex(), () -> Optional.of(
                new Tuple<>(sim.getEventLog(offset, count), sim.getNumOfEventsInLog()))
        );
    }

    /**
     * Return the number of events in simulation event log
     *
     * @param uuid sim uuid
     * @return total number of events or 0
     */
    public long totalEvents(String uuid) {
        final Simulation sim = simulationToState.get(uuid);
        if (sim == null) return 0;
        return withLock(sim.getMutex(), sim::getNumOfEventsInLog);
    }


    /**
     * @return number of processors available to jvm
     */
    private int nproc() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Lock mutex, then access, then unlock
     *
     * @param lock     the mutex lock
     * @param supplier the func to run once locked
     * @return the return type of func
     */
    private static <T> T withLock(Lock lock, Supplier<T> supplier) {
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Private members
     */
    private final Executor executor;
    private final SimulationService simulationService;
    private final ConcurrentHashMap<String, Simulation> simulationToState;
}
