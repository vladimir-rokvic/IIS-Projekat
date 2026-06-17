package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.DocumentResponse;

import com.iis.projekat.model.Beneficiary.AidType;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import com.iis.projekat.model.Beneficiary.BeneficiaryDocument;
import com.iis.projekat.model.Beneficiary.DocumentTypeBeneficiary;

import com.iis.projekat.repository.Beneficiary.BeneficiaryDocumentRepository;
import com.iis.projekat.repository.Beneficiary.BeneficiaryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeneficiaryDocumentService {

    private final BeneficiaryDocumentRepository dokumentRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    @Value("${app.upload.dir:./documents}")
    private String uploadDir;

    public DocumentResponse upload(Long korisnikId, DocumentTypeBeneficiary tip, MultipartFile fajl) throws IOException {
        Beneficiary korisnik = beneficiaryRepository.findById(korisnikId)
                .orElseThrow(() -> new EntityNotFoundException("Korisnik nije pronađen"));

        String originalniNaziv = fajl.getOriginalFilename();
        String ekstenzija = getEkstenzija(originalniNaziv);
        validateFormat(ekstenzija);

        Path direktorijum = Paths.get(uploadDir, korisnikId.toString());
        Files.createDirectories(direktorijum);

        String noviNaziv = tip.name().toLowerCase() + "_" + UUID.randomUUID() + "." + ekstenzija;
        Path putanja = direktorijum.resolve(noviNaziv);
        Files.copy(fajl.getInputStream(), putanja);

        BeneficiaryDocument dokument = BeneficiaryDocument.builder()
                .tip(tip)
                .putanjaFajla(putanja.toString())
                .nazivFajla(originalniNaziv)
                .aktivan(true)
                .korisnik(korisnik)
                .build();

        beneficiaryRepository.save(korisnik);
        validateUploaded(korisnik);
        dokumentRepository.save(dokument);
        return toResponse(dokument);
    }

    public ResponseEntity<Resource> getFile(Long id) {
        BeneficiaryDocument dokument = dokumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dokument nije pronađen"));

        Path putanja = Paths.get(dokument.getPutanjaFajla());
        Resource resource = new FileSystemResource(putanja);

        if (!resource.exists()) {
            throw new EntityNotFoundException("Fajl nije pronađen na disku");
        }

        String contentType;
        try {
            contentType = Files.probeContentType(putanja);
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + dokument.getNazivFajla() + "\"")
                .body(resource);
    }

    private void validateUploaded(Beneficiary korisnik) {

        List<DocumentResponse> docs = getAktivniDokumenti(korisnik.getId());

        boolean licna = false;
        boolean nezaposlenost = false;
        boolean prihodi = false;
        boolean medicine = false;

        for (DocumentResponse doc : docs) {
            if (doc.getTip() == DocumentTypeBeneficiary.LICNA_KARTA) licna = true;
            if (doc.getTip() == DocumentTypeBeneficiary.POTVRDA_O_NEZAPOSLENOSTI) nezaposlenost = true;
            if (doc.getTip() == DocumentTypeBeneficiary.POTVRDA_O_PRIHODIMA) prihodi = true;
            if (doc.getTip() == DocumentTypeBeneficiary.MEDICINSKA_DOKUMENTACIJA) medicine = true;
        }

        boolean baseValid = licna && nezaposlenost && prihodi;

        boolean eligible;

        if (korisnik.getType() == AidType.MEDICINE) {
            eligible = baseValid && medicine;
        } else {
            eligible = baseValid;
        }

        korisnik.setEligible(eligible);
        beneficiaryRepository.save(korisnik);
    }

    public List<DocumentResponse> getAktivniDokumenti(Long korisnikId) {
        return dokumentRepository.findByKorisnikIdAndAktivan(korisnikId, true)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void obrisi(Long dokumentId) {
        BeneficiaryDocument dokument = dokumentRepository.findById(dokumentId)
                .orElseThrow(() -> new EntityNotFoundException("Dokument nije pronađen"));
        dokument.setAktivan(false);
        Beneficiary korisnik = dokument.getKorisnik();
        korisnik.setEligible(false);
        validateUploaded(korisnik);
        beneficiaryRepository.save(korisnik);
        dokumentRepository.save(dokument);
    }

    private void validateFormat(String extension) {
        List<String> allowed = List.of("pdf", "jpg", "jpeg", "png");
        if (!allowed.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Dozvoljeni formati: PDF, JPG, PNG");
        }
    }

    private String getEkstenzija(String naziv) {
        if (naziv == null || !naziv.contains(".")) {
            throw new IllegalArgumentException("Fajl nema ekstenziju");
        }
        return naziv.substring(naziv.lastIndexOf('.') + 1);
    }

    private DocumentResponse toResponse(BeneficiaryDocument d) {
        return DocumentResponse.builder()
                .id(d.getId())
                .tip(d.getTip())
                .nazivFajla(d.getNazivFajla())
                .aktivan(d.isAktivan())
                .datumUploada(d.getDatumUploada())
                .build();
    }
}