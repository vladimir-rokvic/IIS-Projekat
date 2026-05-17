package com.iis.projekat.repository;

import com.iis.projekat.model.Project;
import com.iis.projekat.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Svi projekti određenog koordinatora
    List<Project> findByKoordinatorId(Long koordinatorId);

    // Projekti vidljivi menadžeru (status SPREMAN_ZA_ODOBRENJE)
    List<Project> findByStatus(ProjectStatus status);
}
