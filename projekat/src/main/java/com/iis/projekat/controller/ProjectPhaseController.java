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

    @PutMapping("/api/faze/{phaseId}/zavrsi")
    public ResponseEntity<ProjectPhaseResponseDTO> zavrshiFazu(
            @PathVariable Long phaseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee koordinator = getUlogovanog(userDetails);
        return ResponseEntity.ok(phaseService.zavrsiFazu(phaseId, koordinator.getId()));
    }

    /**
     * Koordinator predlaže novu fazu projekta (kada su sve faze završene).
     * Projekat prelazi u CEKA_ODOBRENJE_NOVE_FAZE.
     * POST /api/projekti/{id}/nova-faza
     * Body: { "faza": {...}, "razlog": "..." }
     */
    @PostMapping("/api/projekti/{id}/nova-faza")
    public ResponseEntity<ProjectPhaseResponseDTO> predloziNovuFazu(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        Employee koordinator = getUlogovanog(userDetails);

        // Deserijalizuj manuelno iz mape
        @SuppressWarnings("unchecked")
        Map<String, Object> fazaMap = (Map<String, Object>) body.get("faza");
        String razlog = (String) body.get("razlog");

        ProjectPhaseCreateDTO dto = new ProjectPhaseCreateDTO();
        dto.naziv = (String) fazaMap.get("naziv");
        dto.ciljevi = (String) fazaMap.get("ciljevi");
        dto.rokPocetak = (String) fazaMap.get("rokPocetak");
        dto.rokKraj = (String) fazaMap.get("rokKraj");
        dto.brojVolontera = (Integer) fazaMap.get("brojVolontera");
        @SuppressWarnings("unchecked")
        List<Long> vestineIds = (List<Long>) fazaMap.get("potrebneVestineIds");
        dto.potrebneVestineIds = vestineIds;

        return ResponseEntity.ok(phaseService.predloziNovuFazu(id, koordinator.getId(), dto, razlog));
    }

    /**
     * Menadžer odobrava ili odbija predloženu novu fazu.
     * PUT /api/projekti/{id}/nova-faza/odluka
     * Body: { "odobri": true, "razlog": "..." }
     */
    @PutMapping("/api/projekti/{id}/nova-faza/odluka")
    public ResponseEntity<?> odluciONovajFazi(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        boolean odobri = (Boolean) body.get("odobri");
        String razlog = (String) body.getOrDefault("razlog", null);

        ProjectPhaseResponseDTO result = phaseService.odluciONovajFazi(id, odobri, razlog);
        if (result == null) {
            return ResponseEntity.ok(Map.of("poruka", "Nova faza je odbijena i obrisana."));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/faze/{phaseId}/preporuke-volontera")
    public ResponseEntity<com.iis.projekat.dto.VolonterPreporukaResponseDTO> preporuciVolontere(
            @PathVariable Long phaseId) {
        return ResponseEntity.ok(phaseService.preporuciVolontere(phaseId));
    }

}
