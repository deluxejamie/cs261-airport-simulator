package uk.ac.warwick.dcs.airportsimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import uk.ac.warwick.dcs.airportsimulator.entity.EventLogEntryEntity;
import java.util.List;

public interface EventLogEntryRepository extends JpaRepository<EventLogEntryEntity, Long> {
    List<EventLogEntryEntity> findBySimulationIdOrderBySimTimestampAsc(String simulationId, Pageable pageable);
}