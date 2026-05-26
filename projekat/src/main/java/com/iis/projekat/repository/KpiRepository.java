package com.iis.projekat.repository;

import com.iis.projekat.model.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
    Optional<Kpi> findByProjectId(Long projectId);
}
