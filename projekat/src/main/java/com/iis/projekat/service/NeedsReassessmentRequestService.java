package com.iis.projekat.service;

import com.iis.projekat.dto.NeedsReassessmentRequestDTO;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import com.iis.projekat.model.Beneficiary.NeedsReassessmentRequest;
import com.iis.projekat.repository.BeneficiaryRepository;
import com.iis.projekat.repository.NeedsReassessmentRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class NeedsReassessmentRequestService {
    private final NeedsReassessmentRequestRepository needsReassessmentRequestRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    public NeedsReassessmentRequestService(NeedsReassessmentRequestRepository needsReassessmentRequestRepository, BeneficiaryRepository beneficiaryRepository){

        this.needsReassessmentRequestRepository = needsReassessmentRequestRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public NeedsReassessmentRequest save(NeedsReassessmentRequestDTO dto) {

        Optional<Beneficiary> beneficiary = beneficiaryRepository.findById(dto.getBeneficiaryId());
        if (beneficiary.isEmpty()) {
            throw new IllegalArgumentException("Not logged in.");
        }

        if (needsReassessmentRequestRepository
                .existsByBeneficiaryIdAndAddressedFalse(beneficiary.get().getId())) {
            throw new IllegalStateException("There is already an open reassessment request.");
        }

        NeedsReassessmentRequest request = new NeedsReassessmentRequest();
        request.setAddressed(false);
        request.setBeneficiary(beneficiary.get());
        request.setCreationDate(LocalDate.now());

        return needsReassessmentRequestRepository.save(request);
    }
}
