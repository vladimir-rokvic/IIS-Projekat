package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.model.Beneficiary.BeneficiaryDocument;
import com.iis.projekat.repository.Beneficiary.BeneficiaryDocumentRepository;
import com.iis.projekat.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryDocumentExpiryService {

    private final BeneficiaryDocumentRepository beneficiaryDocumentRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *") // Svaki dan u 8:00
    @Transactional
    public void deactivateExpiredDocuments() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        List<BeneficiaryDocument> expiredDocuments = beneficiaryDocumentRepository
                .findAllByAktivanTrueAndDatumUploadaBefore(sixMonthsAgo);

        for (BeneficiaryDocument doc : expiredDocuments) {
            doc.setAktivan(false);

            String email = doc.getKorisnik().getEmail();
            String subject = "Dokument istekao";
            String body = String.format(
                    "Poštovani,\n\nVaš dokument '%s' je istekao (uploadovan: %s) i više nije aktivan.\n\nMolimo Vas da dostavite novi dokument.",
                    doc.getNazivFajla(),
                    doc.getDatumUploada().toLocalDate()
            );

            emailService.sendMail(email, subject, body);
        }

        beneficiaryDocumentRepository.saveAll(expiredDocuments);
    }
}