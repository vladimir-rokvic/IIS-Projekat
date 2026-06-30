package com.iis.projekat.controller;

import com.iis.projekat.dto.DonationCreateDTO;
import com.iis.projekat.dto.DonationDTO;
import com.iis.projekat.model.DocumentType;
import com.iis.projekat.service.DonationReportService;
import com.iis.projekat.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @Autowired
    private DonationReportService donationReportService;

    @PostMapping
    public ResponseEntity<DonationDTO> create(@RequestBody DonationCreateDTO dto) {
        DonationDTO res = donationService.createDonation(dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/with-document")
    public ResponseEntity<DonationDTO> createWithDocument(@RequestBody DonationCreateDTO dto, @RequestParam DocumentType documentType) {
        DonationDTO res = donationService.createDonationWithDocument(dto, documentType);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DonationCreateDTO dto) {
        boolean ok = donationService.updateDonation(id, dto);
        if(!ok) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationDTO> get(@PathVariable Long id) {
        DonationDTO dto = donationService.getDonationById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<DonationDTO>> list() {
        return ResponseEntity.ok(donationService.listDonations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/donor/{donorId}/project/{projectId}")
    public ResponseEntity<DonationDTO> getByDonorAndProject(
            @PathVariable Long donorId,
            @PathVariable Long projectId) {
        return donationService.findByDonorAndProject(donorId, projectId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping(value = "/donation-trends", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadDonationTrendsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        byte[] pdf = donationReportService.generateReport(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "donation-trends-report-" + startDate + "-to-" + endDate + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
