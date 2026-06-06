package com.iis.projekat.controller;

import com.iis.projekat.dto.ProjectPhaseResponseDTO;
import com.iis.projekat.dto.SetFazeRequest;
import com.iis.projekat.dto.SkillTypeDTO;
import com.iis.projekat.model.Employee;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.service.ProjectPhaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectPhaseController {

    private final ProjectPhaseService phaseService;
    private final EmployeeRepository employeeRepository;

    public ProjectPhaseController(ProjectPhaseService phaseService,
                                  EmployeeRepository employeeRepository) {
        this.phaseService = phaseService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Koordinator postavlja faze za odobreni projekat.
     * Zamenjuje sve postojeće faze novom listom.
     * POST /api/projekti/{id}/faze
     */
    @PostMapping("/api/projekti/{id}/faze")
    public ResponseEntity<List<ProjectPhaseResponseDTO>> postaviFaze(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SetFazeRequest req) {

        Employee koordinator = getUlogovanog(userDetails);
        return ResponseEntity.ok(phaseService.postaviFaze(id, koordinator.getId(), req));
    }

    /**
     * Vraća listu faza za određeni projekat.
     * GET /api/projekti/{id}/faze
     */
    @GetMapping("/api/projekti/{id}/faze")
    public ResponseEntity<List<ProjectPhaseResponseDTO>> getFaze(@PathVariable Long id) {
        return ResponseEntity.ok(phaseService.getFaze(id));
    }

    /**
     * Postavlja pomoćne koordinatore na konkretnu fazu.
     * PUT /api/faze/{phaseId}/pomocni-koordinatori
     * Body: { "pomocniKoordinatoriIds": [1, 2, 3] }
     */
    @PutMapping("/api/faze/{phaseId}/pomocni-koordinatori")
    public ResponseEntity<ProjectPhaseResponseDTO> postaviPomocneNaFazu(
            @PathVariable Long phaseId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, List<Long>> body) {

        Employee koordinator = getUlogovanog(userDetails);
        List<Long> pomocniIds = body.get("pomocniKoordinatoriIds");
        return ResponseEntity.ok(
                phaseService.postaviPomocneKoordinatoreNaFazu(phaseId, koordinator.getId(), pomocniIds));
    }

    /**
     * Vraća sve dostupne tipove veština — za dropdown pri kreiranju faza.
     * GET /api/skill-types
     */
    @GetMapping("/api/skill-types")
    public ResponseEntity<List<SkillTypeDTO>> sveVestine() {
        return ResponseEntity.ok(phaseService.sveVestine());
    }

    // Pomoćna metoda
    private Employee getUlogovanog(UserDetails userDetails) {
        return employeeRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("Korisnik nije zaposleni."));
    }
}
