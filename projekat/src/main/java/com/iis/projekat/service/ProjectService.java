package com.iis.projekat.service;

import com.iis.projekat.dto.ProjectResponseDTO;
import com.iis.projekat.dto.UpdateProjectRequest;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(ProjectRepository projectRepository,
                          EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    // ── 1. Kreiranje projekta ────────────────────────────────────────

    /**
     * Kreira novi projekat. Poziva ga koordinator.
     *
     * @param koordinatorId  ID koordinatora koji kreira projekat
     * @param naziv          obavezno
     * @param opis           obavezno
     * @param ciljevi        obavezno
     * @param rokPocetak     obavezno
     * @param rokKraj        obavezno
     * @param ciljnaGrupa    opciono
     * @param geoLokacija    opciono
     * @param finansiranje   opciono
     * @param dokument       obavezno (multipart fajl)
     */
    public ProjectResponseDTO kreirajProjekat(
            Long koordinatorId,
            String naziv,
            String opis,
            String ciljevi,
            LocalDate rokPocetak,
            LocalDate rokKraj,
            String ciljnaGrupa,
            String geoLokacija,
            String finansiranje,
            MultipartFile dokument) throws IOException {

        Employee koordinator = nadjiKoordinatora(koordinatorId);

        if (dokument == null || dokument.isEmpty()) {
            throw new IllegalArgumentException("Dokument sa planom projekta je obavezan.");
        }

        Project p = new Project();
        p.setNaziv(naziv);
        p.setOpis(opis);
        p.setCiljevi(ciljevi);
        p.setRokPocetak(rokPocetak);
        p.setRokKraj(rokKraj);
        p.setKoordinator(koordinator);
        p.setCiljnaGrupa(ciljnaGrupa);
        p.setGeografskaLokacija(geoLokacija);
        p.setIzvoriFinansiranja(finansiranje);
        p.setDokumentIme(dokument.getOriginalFilename());
        p.setDokumentSadrzaj(dokument.getBytes());
        p.setStatus(ProjectStatus.U_PRIPREMI);

        return ProjectResponseDTO.from(projectRepository.save(p));
    }

    // ── 2. Određivanje pomoćnih koordinatora ────────────────────────

    /**
     * Postavlja listu pomoćnih koordinatora na projektu.
     * Ako se prosledi prazna lista, briše sve pomoćne koordinatore.
     */
    public ProjectResponseDTO postaviPomocneKoordinatore(
            Long projektId,
            Long koordinatorId,
            List<Long> pomocniIds) {

        Project p = nadjiProjekat(projektId);
        provjeriVlasnistvo(p, koordinatorId);
        provjeriStatus(p);

        List<Employee> pomocni = pomocniIds == null
                ? List.of()
                : pomocniIds.stream()
                        .map(id -> nadjiKoordinatora(id))
                        .collect(Collectors.toList());

        p.setPomocniKoordinatori(pomocni);
        return ProjectResponseDTO.from(projectRepository.save(p));
    }

    // ── 3. Editovanje projekta ───────────────────────────────────────

    /**
     * Edituje tekstualna polja projekta dok je u statusu U_PRIPREMI.
     */
    public ProjectResponseDTO editujProjekat(
            Long projektId,
            Long koordinatorId,
            UpdateProjectRequest req) {

        Project p = nadjiProjekat(projektId);
        provjeriVlasnistvo(p, koordinatorId);
        provjeriStatus(p);

        if (req.naziv != null)              p.setNaziv(req.naziv);
        if (req.opis != null)               p.setOpis(req.opis);
        if (req.ciljevi != null)            p.setCiljevi(req.ciljevi);
        if (req.rokPocetak != null)         p.setRokPocetak(req.rokPocetak);
        if (req.rokKraj != null)            p.setRokKraj(req.rokKraj);
        if (req.ciljnaGrupa != null)        p.setCiljnaGrupa(req.ciljnaGrupa);
        if (req.geografskaLokacija != null) p.setGeografskaLokacija(req.geografskaLokacija);
        if (req.izvoriFinansiranja != null)  p.setIzvoriFinansiranja(req.izvoriFinansiranja);
        if (req.status != null) p.setStatus(ProjectStatus.valueOf(req.status));

        if (req.pomocniKoordinatoriIds != null) {
            List<Employee> pomocni = req.pomocniKoordinatoriIds.stream()
                    .map(id -> nadjiKoordinatora(id))
                    .collect(Collectors.toList());
            p.setPomocniKoordinatori(pomocni);
        }

        return ProjectResponseDTO.from(projectRepository.save(p));
    }

    /**
     * Zamjena dokumenta dok je projekat u statusu U_PRIPREMI.
     */
    public ProjectResponseDTO zamijeniDokument(
            Long projektId,
            Long koordinatorId,
            MultipartFile dokument) throws IOException {

        Project p = nadjiProjekat(projektId);
        provjeriVlasnistvo(p, koordinatorId);
        provjeriStatus(p);

        if (dokument == null || dokument.isEmpty()) {
            throw new IllegalArgumentException("Novi dokument ne može biti prazan.");
        }

        p.setDokumentIme(dokument.getOriginalFilename());
        p.setDokumentSadrzaj(dokument.getBytes());
        return ProjectResponseDTO.from(projectRepository.save(p));
    }

    // ── 4. Promjena statusa u Spreman za odobrenje ───────────────────

    /**
     * Koordinator šalje projekat na odobrenje.
     * Nakon toga, projekat je zaključan za izmjene i vidljiv menadžeru.
     */
    public ProjectResponseDTO posaljiNaOdobrenje(Long projektId, Long koordinatorId) {
        Project p = nadjiProjekat(projektId);
        provjeriVlasnistvo(p, koordinatorId);
        provjeriStatus(p);

        p.setStatus(ProjectStatus.SPREMAN_ZA_ODOBRENJE);
        return ProjectResponseDTO.from(projectRepository.save(p));
    }

    // ── 5. Čitanje podataka ──────────────────────────────────────────

    /** Svi projekti određenog koordinatora. */
    public List<ProjectResponseDTO> projektiKoordinatora(Long koordinatorId) {
        return projectRepository.findByKoordinatorId(koordinatorId)
                .stream()
                .map(ProjectResponseDTO::from)
                .collect(Collectors.toList());
    }

    /** Jedan projekat po ID-u. */
    public ProjectResponseDTO getProjekat(Long projektId) {
        return ProjectResponseDTO.from(nadjiProjekat(projektId));
    }

    /** Preuzimanje dokumenta (vraća bytes za download). */
    public Project getProjekatEntitet(Long projektId) {
        return nadjiProjekat(projektId);
    }

    /** Svi koordinatori u sistemu — za odabir pomoćnih. */
    public List<Employee> sviKoordinatori() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getEmployeeType() == EmployeeType.COORDINATOR)
                .collect(Collectors.toList());
    }

    // ── Privatne pomoćne metode ──────────────────────────────────────

    private Project nadjiProjekat(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projekat sa ID=" + id + " ne postoji."));
    }

    private Employee nadjiKoordinatora(Long id) {
        Employee e = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zaposleni sa ID=" + id + " ne postoji."));
        if (e.getEmployeeType() != EmployeeType.COORDINATOR) {
            throw new IllegalArgumentException("Zaposleni ID=" + id + " nije koordinator.");
        }
        return e;
    }

    private void provjeriVlasnistvo(Project p, Long koordinatorId) {
        if (!p.getKoordinator().getId().equals(koordinatorId)) {
            throw new SecurityException("Nemate pravo da mijenjate ovaj projekat.");
        }
    }

    private void provjeriStatus(Project p) {
        if (p.getStatus() != ProjectStatus.U_PRIPREMI) {
            throw new IllegalStateException(
                    "Projekat nije u statusu U_PRIPREMI — izmjene nisu dozvoljene.");
        }
    }
}
