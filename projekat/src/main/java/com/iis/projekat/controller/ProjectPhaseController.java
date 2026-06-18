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
import com.iis.projekat.dto.ProjectPhaseCreateDTO;

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
     * Koristi se samo za inicijalno postavljanje kompletnog plana faza.
     * Ne koristiti za dodavanje pojedinačne faze jer briše postojeće faze.
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
     */
    @GetMapping("/api/projekti/{id}/faze")
    public ResponseEntity<List<ProjectPhaseResponseDTO>> getFaze(@PathVariable Long id) {
        return ResponseEntity.ok(phaseService.getFaze(id));
    }

    /**
     * Postavlja pomoćne koordinatore na konkretnu fazu.
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

    @PutMapping("/api/faze/{phaseId}/zavrsi")
    public ResponseEntity<ProjectPhaseResponseDTO> zavrsiFazu(
            @PathVariable Long phaseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee koordinator = getUlogovanog(userDetails);
        return ResponseEntity.ok(phaseService.zavrsiFazu(phaseId, koordinator.getId()));
    }

    /**
     * Dodavanje nove faze u projektu.
     */
    @PostMapping("/api/projekti/{id}/nova-faza")
    public ResponseEntity<ProjectPhaseResponseDTO> dodajNovuFazu(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        Employee koordinator = getUlogovanog(userDetails);

        // Deserijalizuj manuelno iz mape
        @SuppressWarnings("unchecked")
        Map<String, Object> fazaMap = (Map<String, Object>) body.get("faza");

        ProjectPhaseCreateDTO dto = new ProjectPhaseCreateDTO();
        dto.naziv = (String) fazaMap.get("naziv");
        dto.ciljevi = (String) fazaMap.get("ciljevi");
        dto.rokPocetak = (String) fazaMap.get("rokPocetak");
        dto.rokKraj = (String) fazaMap.get("rokKraj");
        dto.brojVolontera = (Integer) fazaMap.get("brojVolontera");
        @SuppressWarnings("unchecked")
        List<Long> vestineIds = (List<Long>) fazaMap.get("potrebneVestineIds");
        dto.potrebneVestineIds = vestineIds;

        return ResponseEntity.ok(phaseService.predloziNovuFazu(id, koordinator.getId(), dto));
    }


    @GetMapping("/api/faze/{phaseId}/preporuke-volontera")
    public ResponseEntity<com.iis.projekat.dto.VolonterPreporukaResponseDTO> preporuciVolontere(
            @PathVariable Long phaseId) {
        return ResponseEntity.ok(phaseService.preporuciVolontere(phaseId));
    }

    /**
     * Ažurira podatke jedne faze (naziv, ciljevi, rokovi, veštine, broj volontera).
     * Taskovi, zavrsena flag i ID ostaju netaknuti.
     */
    @PutMapping("/api/faze/{id}")
    public ResponseEntity<ProjectPhaseResponseDTO> azurirajFazu(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectPhaseCreateDTO dto) {

        Employee koordinator = getUlogovanog(userDetails);
        return ResponseEntity.ok(phaseService.azurirajFazu(id, koordinator.getId(), dto));
    }

}
