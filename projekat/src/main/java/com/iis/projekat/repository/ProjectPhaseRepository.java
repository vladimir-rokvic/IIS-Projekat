package com.iis.projekat.repository;

import com.iis.projekat.model.ProjectPhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectPhaseRepository extends JpaRepository<ProjectPhase, Long> {
    List<ProjectPhase> findByProjectIdOrderByRedosled(Long projectId);
}
