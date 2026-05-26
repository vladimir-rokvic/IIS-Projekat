package com.iis.projekat.controller;

import com.iis.projekat.dto.ManagerReviewRequest;
import com.iis.projekat.dto.ProjectResponseDTO;
import com.iis.projekat.dto.UpdateProjectRequest;
import com.iis.projekat.model.Employee;
import com.iis.projekat.model.EmployeeType;
import com.iis.projekat.model.Project;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.service.ProjectService;
import com.iis.projekat.dto.KpiRequest;
import com.iis.projekat.dto.KpiResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projekti")
public class ProjectController {

    private final ProjectService projectService;
    private final EmployeeRepository employeeRepository;

    public ProjectController(ProjectService projectService,
                             EmployeeRepository employeeRepository) {
        this.projectService = projectService;
        this.employeeRepository = employeeRepository;
    }

    // Pomoćna metoda: izvuci Employee iz JWT principal-a
    private Employee getUlogovanogZaposlenog(UserDetails userDetails) {
        return employeeRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("Korisnik nije zaposleni."));
    }

    /**
     * Kreiranje projekta.
     * Šalje se kao multipart/form-data jer ima i fajl.
     *
     * Primjer (Postman form-data):
     *   naziv=Test projekat
     *   opis=Opis projekta
     *   ciljevi=Ciljevi projekta
     *   rokPocetak=2025-09-01
     *   rokKraj=2025-12-31
     *   dokument=(file)
     *   // ostala opciona polja po potrebi
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectResponseDTO> kreirajProjekat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String naziv,
            @RequestParam String opis,
            @RequestParam String ciljevi,
            @RequestParam String rokPocetak,
            @RequestParam String rokKraj,
            @RequestParam(required = false) String ciljnaGrupa,
            @RequestParam(required = false) String geografskaLokacija,
            @RequestParam(required = false) String izvoriFinansiranja,
            @RequestPart("dokument") MultipartFile dokument) throws IOException {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);

        ProjectResponseDTO dto = projectService.kreirajProjekat(
                koordinator.getId(),
                naziv,
                opis,
                ciljevi,
                LocalDate.parse(rokPocetak),
                LocalDate.parse(rokKraj),
                ciljnaGrupa,
                geografskaLokacija,
                izvoriFinansiranja,
                dokument
        );

        return ResponseEntity.ok(dto);
    }

    /** Koordinator vidi svoje projekte. */
    @GetMapping("/moji")
    public ResponseEntity<List<ProjectResponseDTO>> mojiProjekti(
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(projectService.projektiKoordinatora(koordinator.getId()));
    }

    @GetMapping("/svi")
    public ResponseEntity<List<ProjectResponseDTO>> sviProjekti(
            @AuthenticationPrincipal UserDetails userDetails) {
        provjeriManagera(userDetails);
        return ResponseEntity.ok(projectService.sviProjekti());
    }

    /**
     * Lista svih projekata za dropdownove i ostale selektore.
     * Namjerno je read-only i ne ograničava se na menadžera.
     */
    @GetMapping("/sve")
    public ResponseEntity<List<ProjectResponseDTO>> sviProjektiZaOdabir() {
        return ResponseEntity.ok(projectService.sviProjekti());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjekat(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjekat(id));
    }


    /** Editovanje tekstualnih polja i pomoćnih koordinatora. */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> editujProjekat(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProjectRequest req) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(projectService.editujProjekat(id, koordinator.getId(), req));
    }


    /** Zamjena dokumenta (zasebni endpoint jer je multipart). */
    @PutMapping(value = "/{id}/dokument", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectResponseDTO> zamijeniDokument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("dokument") MultipartFile dokument) throws IOException {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(projectService.zamijeniDokument(id, koordinator.getId(), dokument));
    }


    /** Postavljanje liste pomoćnih koordinatora. */
    @PutMapping("/{id}/pomocni-koordinatori")
    public ResponseEntity<ProjectResponseDTO> postaviPomocne(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, List<Long>> body) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        List<Long> pomocniIds = body.get("pomocniKoordinatoriIds");
        return ResponseEntity.ok(
                projectService.postaviPomocneKoordinatore(id, koordinator.getId(), pomocniIds));
    }


    /** Promjena statusa u SPREMAN_ZA_ODOBRENJE. */
    @PutMapping("/{id}/spreman")
    public ResponseEntity<ProjectResponseDTO> posaljiNaOdobrenje(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(projectService.posaljiNaOdobrenje(id, koordinator.getId()));
    }


    /** Preuzimanje dokumenta kao fajl (download). */
    @GetMapping("/{id}/dokument")
    public ResponseEntity<byte[]> preuzmiDokument(@PathVariable Long id) {
        Project p = projectService.getProjekatEntitet(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + p.getDokumentIme() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(p.getDokumentSadrzaj());
    }


    /**
     * Lista svih koordinatora u sistemu.
     * Frontend je koristi za odabir pomoćnih koordinatora.
     */
    @GetMapping("/koordinatori")
    public ResponseEntity<?> sviKoordinatori() {
        List<Employee> koordinatori = projectService.sviKoordinatori();
        List<Map<String, Object>> result = koordinatori.stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getId(),
                        "ime", e.getName(),
                        "prezime", e.getSurname(),
                        "email", e.getEmail()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Menadžer donosi odluku o projektu.
     * Body: { "status": "ODOBREN" | "NEOPHODNA_IZMENA" | "ODBIJEN", "razlog": "..." }
     * razlog je obavezan za NEOPHODNA_IZMENA i ODBIJEN.
     */
    @PutMapping("/{id}/odluka")
    public ResponseEntity<ProjectResponseDTO> odluciOProjektu(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ManagerReviewRequest req) {
        provjeriManagera(userDetails);
        return ResponseEntity.ok(projectService.odluciOProjektu(id, req));
    }

    private void provjeriManagera(UserDetails userDetails) {
        Employee e = getUlogovanogZaposlenog(userDetails);
        if (e.getEmployeeType() != EmployeeType.MANAGER) {
            throw new SecurityException("Samo menadžer može da izvrši ovu akciju.");
        }
    }

    @PutMapping("/{id}/kpi")
    public ResponseEntity<KpiResponseDTO> saveKpi(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody KpiRequest req) {
        Employee koordinator = getUlogovanogZaposlenog(userDetails);
        return ResponseEntity.ok(projectService.saveKpi(id, koordinator.getId(), req));
    }

    // endpoint za čitanje KPI (koristi ProjectAcceptedPage pri učitavanju)
    @GetMapping("/{id}/kpi")
    public ResponseEntity<KpiResponseDTO> getKpi(@PathVariable Long id) {
        KpiResponseDTO kpi = projectService.getKpi(id);
        if (kpi == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(kpi);
    }

    @GetMapping("/odobreni")
    public ResponseEntity<List<ProjectResponseDTO>> odobreniProjekti() {
        return ResponseEntity.ok(projectService.odobreniProjekti());
    }

}
