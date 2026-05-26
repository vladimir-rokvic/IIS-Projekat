package com.iis.projekat.repository;

import com.iis.projekat.model.NeedsReassessmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeedsReassessmentRequestRepository extends JpaRepository<NeedsReassessmentRequest,Long> {
    boolean existsByBeneficiaryIdAndAddressedFalse(Long beneficiaryId);
}
