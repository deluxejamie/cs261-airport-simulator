package uk.ac.warwick.dcs.airportsimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.warwick.dcs.airportsimulator.entity.SimulationResultEntity;

public interface SimulationResultRepository extends JpaRepository<SimulationResultEntity, String> {}