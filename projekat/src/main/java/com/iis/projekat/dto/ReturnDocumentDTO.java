package com.iis.projekat.dto;

import com.iis.projekat.model.DocumentStatus;
import com.iis.projekat.model.ReturnDocument;
import com.iis.projekat.model.DocumentType;


import java.time.LocalDate;

public class ReturnDocumentDTO {
    private Long id;
    private LocalDate issuedDate;
    private String content;
    private DocumentType documentType;
    private DocumentStatus documentStatus;
    private Long donationId;

    public ReturnDocumentDTO() {}

    public ReturnDocumentDTO(ReturnDocument rd) {
        this.id = rd.getId();
        this.issuedDate = rd.getIssuedDate();
        this.content = rd.getContent();
        this.documentType = rd.getDocumentType();
        this.documentStatus = rd.getDocumentStatus();
        if(rd.getDonation() != null) this.donationId = rd.getDonation().getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

    public Long getDonationId() { return donationId; }
    public void setDonationId(Long donationId) { this.donationId = donationId; }

    public DocumentStatus getDocumentStatus() { return documentStatus; }
    public void setDocumentStatus(DocumentStatus documentStatus) { this.documentStatus = documentStatus; }
}