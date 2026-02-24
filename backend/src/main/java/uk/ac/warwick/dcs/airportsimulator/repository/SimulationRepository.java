package uk.ac.warwick.dcs.airportsimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationEntity;

public interface SimulationRepository extends JpaRepository<SimulationEntity, String> {}