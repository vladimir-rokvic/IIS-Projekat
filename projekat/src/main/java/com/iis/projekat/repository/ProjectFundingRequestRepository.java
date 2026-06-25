package com.iis.projekat.repository;

import com.iis.projekat.model.FundingRequestStatus;
import com.iis.projekat.model.ProjectFundingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectFundingRequestRepository extends JpaRepository<ProjectFundingRequest, Long> {
    List<ProjectFundingRequest> findByProjectId(Long projectId);
    List<ProjectFundingRequest> findByKoordinatorId(Long koordinatorId);
    List<ProjectFundingRequest> findByStatus(FundingRequestStatus status);
    List<ProjectFundingRequest> findByProjectIdAndKoordinatorId(Long projectId, Long koordinatorId);
}
