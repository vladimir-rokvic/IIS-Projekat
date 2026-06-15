package com.iis.projekat.repository;

import com.iis.projekat.model.Donation;
import com.iis.projekat.model.ReturnDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnDocumentRepository extends JpaRepository<ReturnDocument, Long> {
    Optional<ReturnDocument> findByDonation_Id(Long donationId);
}
