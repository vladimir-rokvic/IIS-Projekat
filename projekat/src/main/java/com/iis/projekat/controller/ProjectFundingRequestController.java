package com.iis.projekat.controller;

import com.iis.projekat.dto.FundingRequestCreateDTO;
import com.iis.projekat.dto.FundingRequestResponseDTO;
import com.iis.projekat.dto.ManagerFundingDecisionDTO;
import com.iis.projekat.model.Employee;
import com.iis.projekat.model.EmployeeType;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.service.ProjectFundingRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zahtevi-za-sredstvima")
public class ProjectFundingRequestController {

    private final ProjectFundingRequestService fundingService;
    private final EmployeeRepository employeeRepository;

    public ProjectFundingRequestController(ProjectFundingRequestService fundingService,
                                           EmployeeRepository employeeRepository) {
        this.fundingService = fundingService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Koordinator šalje novi zahtev za dodatna sredstva.
     * POST /api/zahtevi-za-sredstvima
     */
    @PostMapping
    public ResponseEntity<FundingRequestResponseDTO> posaljiZahtev(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FundingRequestCreateDTO dto) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(fundingService.posaljiZahtev(koordinator.getId(), dto));
    }

    /**
     * Koordinator vidi sve svoje zahteve (svi projekti).
     * GET /api/zahtevi-za-sredstvima/moji
     */
    @GetMapping("/moji")
    public ResponseEntity<List<FundingRequestResponseDTO>> mojiZahtevi(
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(fundingService.zahteviKoordinatora(koordinator.getId()));
    }

    /**
     * Koordinator ili menadžer vidi zahteve za konkretan projekat.
     * Menadžer prolazi bez provere vlasništva nad projektom.
     * GET /api/zahtevi-za-sredstvima/projekat/{projectId}
     */
    @GetMapping("/projekat/{projectId}")
    public ResponseEntity<List<FundingRequestResponseDTO>> zahteviZaProjekat(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee zaposleni = getUlogovanogZaposlenog(userDetails);
        if (zaposleni.getEmployeeType() == EmployeeType.MANAGER) {
            // Menadžer vidi sve zahteve za projekat bez provere pristupa
            return ResponseEntity.ok(fundingService.zahteviZaProjekatBezProvere(projectId));
        }
        return ResponseEntity.ok(fundingService.zahteviZaProjekat(projectId, zaposleni.getId()));
    }

    /**
     * Menadžer vidi sve zahteve koji čekaju odluku — sa stanjem opštih sredstava.
     * GET /api/zahtevi-za-sredstvima/na-cekanju
     */
    @GetMapping("/na-cekanju")
    public ResponseEntity<List<FundingRequestResponseDTO>> zahteviNaCekanju(
            @AuthenticationPrincipal UserDetails userDetails) {

        provjeriManagera(userDetails);
        return ResponseEntity.ok(fundingService.sviZahteviNaCekanju());
    }

    /**
     * Menadžer vidi sve zahteve (svi statusi).
     * GET /api/zahtevi-za-sredstvima/svi
     */
    @GetMapping("/svi")
    public ResponseEntity<List<FundingRequestResponseDTO>> sviZahtevi(
            @AuthenticationPrincipal UserDetails userDetails) {

        provjeriManagera(userDetails);
        return ResponseEntity.ok(fundingService.sviZahtevi());
    }

    /**
     * Menadžer donosi odluku o zahtevu (odobrava ceo iznos, deo iznosa ili odbija).
     * PUT /api/zahtevi-za-sredstvima/{id}/odluka
     */
    @PutMapping("/{id}/odluka")
    public ResponseEntity<FundingRequestResponseDTO> odluciOZahtevu(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ManagerFundingDecisionDTO odluka) {

        provjeriManagera(userDetails);
        return ResponseEntity.ok(fundingService.odluciOZahtevu(id, odluka));
    }

    // --- Pomoćne metode ---

    private Employee getUlogovanogZaposlenog(UserDetails userDetails) {
        return employeeRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("Korisnik nije zaposleni."));
    }

    private void provjeriManagera(UserDetails userDetails) {
        Employee e = getUlogovanogZaposlenog(userDetails);
        if (e.getEmployeeType() != EmployeeType.MANAGER) {
            throw new SecurityException("Samo menadžer može da izvrši ovu akciju.");
        }
    }
}
