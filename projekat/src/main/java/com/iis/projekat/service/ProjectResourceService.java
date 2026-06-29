package com.iis.projekat.service;

import com.iis.projekat.dto.ProjectResourceDTO;
import com.iis.projekat.model.Employee;
import com.iis.projekat.model.EmployeeType;
import com.iis.projekat.model.Project;
import com.iis.projekat.model.ProjectResource;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.repository.ProjectRepository;
import com.iis.projekat.repository.ProjectResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectResourceService {

    private final ProjectResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectResourceService(ProjectResourceRepository resourceRepository,
                                  ProjectRepository projectRepository,
                                  EmployeeRepository employeeRepository) {
        this.resourceRepository = resourceRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Koordinator vidi sve namenske resurse za konkretan projekat.
     * Proverava se da li je koordinator vlasnik ili pomoćni koordinator projekta.
     */
    public List<ProjectResourceDTO> resursiZaProjekat(Long projectId, Long koordinatorId) {
        Project p = nadjiProjekat(projectId);
        provjeriPristupKoordinatora(p, koordinatorId);
        return resourceRepository.findByProjectId(projectId)
                .stream()
                .map(ProjectResourceDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Menadžer dodaje namenske resurse projektu.
     */
    public ProjectResourceDTO dodajResurs(Long projectId, String naziv, Double iznos, String opis) {
        Project p = nadjiProjekat(projectId);

        ProjectResource r = new ProjectResource();
        r.setProject(p);
        r.setNaziv(naziv);
        r.setUkupnoSredstava(iznos);
        r.setDostupnoSredstava(iznos);
        r.setOpis(opis);

        return ProjectResourceDTO.from(resourceRepository.save(r));
    }

    /**
     * Menadžer ažurira resurse projekta (npr. povećava iznos nakon odobrenja zahteva).
     * Interno se poziva iz FundingRequestService.
     */
    public ProjectResourceDTO povecajDostupnoSredstava(Long resourceId, Double dodatniIznos) {
        ProjectResource r = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resurs ID=" + resourceId + " ne postoji."));
        r.setDostupnoSredstava(r.getDostupnoSredstava() + dodatniIznos);
        r.setUkupnoSredstava(r.getUkupnoSredstava() + dodatniIznos);
        return ProjectResourceDTO.from(resourceRepository.save(r));
    }

    // Pomoćne metode

    private Project nadjiProjekat(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projekat sa ID=" + id + " ne postoji."));
    }

    private void provjeriPristupKoordinatora(Project p, Long koordinatorId) {
        boolean jeVlasnik = p.getKoordinator().getId().equals(koordinatorId);
        boolean jePomocni = p.getPomocniKoordinatori().stream()
                .anyMatch(e -> e.getId().equals(koordinatorId));
        if (!jeVlasnik && !jePomocni) {
            throw new SecurityException("Nemate pravo pristupa resursima ovog projekta.");
        }
    }
}
