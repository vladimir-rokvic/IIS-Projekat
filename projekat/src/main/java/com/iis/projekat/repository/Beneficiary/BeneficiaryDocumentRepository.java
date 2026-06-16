package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.model.Beneficiary.BeneficiaryDocument;
import com.iis.projekat.model.Beneficiary.DocumentTypeBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BeneficiaryDocumentRepository extends JpaRepository<BeneficiaryDocument, Long> {

    List<BeneficiaryDocument> findByKorisnikIdAndAktivan(Long korisnikId, boolean aktivan);

    List<BeneficiaryDocument> findByKorisnikIdAndTipAndAktivan(Long korisnikId, DocumentTypeBeneficiary tip, boolean aktivan);

    boolean existsByKorisnikIdAndTipAndAktivan(Long korisnikId, DocumentTypeBeneficiary tip, boolean aktivan);
}