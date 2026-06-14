package com.iis.projekat.service;

import com.iis.projekat.dto.DonationCreateDTO;
import com.iis.projekat.dto.DonationDTO;
import com.iis.projekat.dto.ReturnDocumentCreateDTO;
import com.iis.projekat.dto.ReturnDocumentDTO;
import com.iis.projekat.model.Donation;
import com.iis.projekat.model.Donor;
import com.iis.projekat.model.Project;
import com.iis.projekat.model.ReturnDocument;
import com.iis.projekat.repository.DonationRepository;
import com.iis.projekat.repository.DonorRepository;
import com.iis.projekat.repository.ProjectRepository;
import com.iis.projekat.repository.ReturnDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnDocumentService {

    @Autowired
    private ReturnDocumentRepository returnDocumentRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DtoMapperService mapper;

    public ReturnDocumentDTO createReturnDocument(ReturnDocumentCreateDTO dto) {
        ReturnDocument rd = new ReturnDocument();
        rd.setIssuedDate(dto.getIssuedDate());
        rd.setContent(dto.getContent());
        rd.setDocumentType(dto.getDocumentType());
        rd.setDocumentStatus(dto.getDocumentStatus());

        if(dto.getDonationId() != null) {
            Donation donation = donationRepository.findById(dto.getDonationId()).orElse(null);
            rd.setDonation(donation);
        }

        returnDocumentRepository.save(rd);
        return mapper.toReturnDocumentDto(rd);
    }

    public boolean updateReturnDocument(Long id, ReturnDocumentCreateDTO dto) {
        ReturnDocument rd = returnDocumentRepository.findById(id).orElse(null);
        if(rd == null) return false;

        rd.setIssuedDate(dto.getIssuedDate());
        rd.setContent(dto.getContent());
        rd.setDocumentType(dto.getDocumentType());
        rd.setDocumentStatus(dto.getDocumentStatus());

        if(dto.getDonationId() != null) {
            Donation donation = donationRepository.findById(dto.getDonationId()).orElse(null);
            rd.setDonation(donation);
        }

        returnDocumentRepository.save(rd);
        return true;
    }

    public ReturnDocumentDTO getReturnDocumentById(Long id) {
        ReturnDocument rd = returnDocumentRepository.findById(id).orElseThrow();
        return mapper.toReturnDocumentDto(rd);
    }

    public List<ReturnDocumentDTO> listReturnDocuments() {
        return returnDocumentRepository.findAll().stream().map(mapper::toReturnDocumentDto).collect(Collectors.toList());
    }

    public void deleteReturnDocument(Long id) { returnDocumentRepository.deleteById(id); }

    public java.util.Optional<ReturnDocumentDTO> findByDonation(Long donationId) {
        return returnDocumentRepository.findByDonation_Id(donationId)
                .map(mapper::toReturnDocumentDto);
    }
}
