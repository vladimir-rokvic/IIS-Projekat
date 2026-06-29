package com.iis.projekat.controller;

import com.iis.projekat.dto.ProjectResourceDTO;
import com.iis.projekat.model.Employee;
import com.iis.projekat.model.EmployeeType;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.service.ProjectResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resursi")
public class ProjectResourceController {

    private final ProjectResourceService resourceService;
    private final EmployeeRepository employeeRepository;

    public ProjectResourceController(ProjectResourceService resourceService,
                                     EmployeeRepository employeeRepository) {
        this.resourceService = resourceService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Koordinator vidi sve namenske resurse za konkretan projekat.
     * GET /api/resursi/projekat/{projectId}
     */
    @GetMapping("/projekat/{projectId}")
    public ResponseEntity<List<ProjectResourceDTO>> resursiZaProjekat(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(resourceService.resursiZaProjekat(projectId, koordinator.getId()));
    }

    /**
     * Menadžer dodaje namenske resurse projektu.
     * POST /api/resursi/projekat/{projectId}
     */
    @PostMapping("/projekat/{projectId}")
    public ResponseEntity<ProjectResourceDTO> dodajResurs(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String naziv,
            @RequestParam Double iznos,
            @RequestParam(required = false) String opis) {

        provjeriManagera(userDetails);
        return ResponseEntity.ok(resourceService.dodajResurs(projectId, naziv, iznos, opis));
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
