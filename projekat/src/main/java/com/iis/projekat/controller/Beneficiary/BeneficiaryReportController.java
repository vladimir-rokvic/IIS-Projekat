package com.iis.projekat.controller.Beneficiary;


import com.iis.projekat.service.Beneficiary.CoveragePdfGenerator;
import com.iis.projekat.service.Beneficiary.EfficiencyPdfGenerator;
import com.iis.projekat.service.Beneficiary.ImpactPdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryReportController {

    private final CoveragePdfGenerator coverateGenerator;
    private final EfficiencyPdfGenerator efficiencyPdfGenerator;
    private final ImpactPdfGenerator impactPdfGenerator;

    //  1. Izvestaj o pokrivenosti
    @GetMapping("/coverage")
    public ResponseEntity<byte[]> coverageReport(
            @RequestParam(name = "period", defaultValue = "month") String periodParam)
            throws IOException {

        byte[] pdf = coverateGenerator.generateCoverageReport(periodParam);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=coverage-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }

    //  2. Izvestaj o efikasnosti distriubicje
    @GetMapping("/efficiency")
    public ResponseEntity<byte[]> efficiencyReport()
            throws IOException {

        byte[] pdf = efficiencyPdfGenerator.generate();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=efficiency-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }

    //  3. Izvestaj o uticaju pomoci
    @GetMapping("/impact")
    public ResponseEntity<byte[]> impactReport()
            throws IOException {

        byte[] pdf = impactPdfGenerator.generate();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=impact-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }
}