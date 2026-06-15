package com.iis.projekat.service;

import com.iis.projekat.dto.DonationDTO;
import com.iis.projekat.dto.DonorDTO;
import com.iis.projekat.dto.ReturnDocumentDTO;
import com.iis.projekat.model.Donation;
import com.iis.projekat.model.Donor;
import com.iis.projekat.model.ReturnDocument;
import org.springframework.stereotype.Service;

@Service
public class DtoMapperService {

    public DonorDTO toDonorDto(Donor d) {
        if(d == null) return null;
        return new DonorDTO(d);
    }

    public DonationDTO toDonationDto(Donation d) {
        if(d == null) return null;
        return new DonationDTO(d);
    }

    public ReturnDocumentDTO toReturnDocumentDto(ReturnDocument rd) {
        if(rd == null) return null;
        return new ReturnDocumentDTO(rd);
    }
}
