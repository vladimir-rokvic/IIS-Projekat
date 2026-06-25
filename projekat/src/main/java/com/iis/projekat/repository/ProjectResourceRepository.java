package com.iis.projekat.repository;

import com.iis.projekat.model.ProjectResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectResourceRepository extends JpaRepository<ProjectResource, Long> {
    List<ProjectResource> findByProjectId(Long projectId);
}
