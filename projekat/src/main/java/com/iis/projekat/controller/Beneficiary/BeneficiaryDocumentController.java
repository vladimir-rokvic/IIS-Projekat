package com.iis.projekat.controller.Beneficiary;

import com.iis.projekat.dto.Beneficiary.DocumentResponse;

import com.iis.projekat.model.Beneficiary.DocumentTypeBeneficiary;
import com.iis.projekat.service.Beneficiary.BeneficiaryDocumentService;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dokumenti")
@RequiredArgsConstructor
public class BeneficiaryDocumentController {

    private final BeneficiaryDocumentService dokumentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam Long korisnikId,
            @RequestParam DocumentTypeBeneficiary tip,
            @RequestParam MultipartFile fajl) throws IOException {

        return ResponseEntity.ok(dokumentService.upload(korisnikId, tip, fajl));
    }

    @GetMapping("/{id}/fajl")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        return dokumentService.getFile(id);
    }

    @GetMapping("/korisnik/{korisnikId}")
    public ResponseEntity<List<DocumentResponse>> getAktivni(@PathVariable Long korisnikId) {
        return ResponseEntity.ok(dokumentService.getAktivniDokumenti(korisnikId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> obrisi(@PathVariable Long id) {
        dokumentService.obrisi(id);
        return ResponseEntity.noContent().build();
    }
}