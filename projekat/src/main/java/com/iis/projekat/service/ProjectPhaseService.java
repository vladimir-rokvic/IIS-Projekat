package com.iis.projekat.service;

import com.iis.projekat.dto.ProjectPhaseCreateDTO;
import com.iis.projekat.dto.ProjectPhaseResponseDTO;
import com.iis.projekat.dto.SetFazeRequest;
import com.iis.projekat.dto.SkillTypeDTO;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.EmployeeRepository;
import com.iis.projekat.repository.ProjectPhaseRepository;
import com.iis.projekat.repository.ProjectRepository;
import com.iis.projekat.repository.SkillTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectPhaseService {

    private final ProjectRepository projectRepository;
    private final ProjectPhaseRepository phaseRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillTypeRepository skillTypeRepository;

    public ProjectPhaseService(ProjectRepository projectRepository,
                               ProjectPhaseRepository phaseRepository,
                               EmployeeRepository employeeRepository,
                               SkillTypeRepository skillTypeRepository) {
        this.projectRepository = projectRepository;
        this.phaseRepository = phaseRepository;
        this.employeeRepository = employeeRepository;
        this.skillTypeRepository = skillTypeRepository;
    }

    /**
     * Koordinator postavlja sve faze projekta odjednom.
     * Briše postojeće faze i upisuje nove — "replace all" semantika.
     * Dozvoljeno samo za projekte u statusu ODOBREN.
     */
    @Transactional
    public List<ProjectPhaseResponseDTO> postaviFaze(Long projectId,
                                                     Long koordinatorId,
                                                     SetFazeRequest req) {
        Project project = nadjiProjekat(projectId);
        provjeriVlasnistvo(project, koordinatorId);
        provjeriStatusOdobren(project);

        if (req.faze == null || req.faze.isEmpty()) {
            throw new IllegalArgumentException("Lista faza ne može biti prazna.");
        }

        // Validacija rokova faza — moraju biti unutar rokova projekta
        for (ProjectPhaseCreateDTO dto : req.faze) {
            LocalDate pocetak = LocalDate.parse(dto.rokPocetak);
            LocalDate kraj = LocalDate.parse(dto.rokKraj);

            if (pocetak.isBefore(project.getRokPocetak()) || kraj.isAfter(project.getRokKraj())) {
                throw new IllegalArgumentException(
                        "Rokovi faze '" + dto.naziv + "' moraju biti unutar rokova projekta ("
                                + project.getRokPocetak() + " – " + project.getRokKraj() + ").");
            }
            if (!pocetak.isBefore(kraj) && !pocetak.isEqual(kraj)) {
                throw new IllegalArgumentException(
                        "Početak faze '" + dto.naziv + "' mora biti pre kraja faze.");
            }
        }

        // Ako se faze ne smeju preklapati, provjeri svaki par
        if (!req.fazeMoguDaSePreklapaju) {
            provjeriNemaPreklapanja(req.faze);
        }

        // Postavi flag na projektu
        project.setFazeMoguDaSePreklapaju(req.fazeMoguDaSePreklapaju);

        // Briši stare faze (orphanRemoval = true + clear lista)
        project.getFaze().clear();
        projectRepository.save(project);

        // Kreiraj nove faze
        List<ProjectPhase> noveFaze = new ArrayList<>();
        for (ProjectPhaseCreateDTO dto : req.faze) {
            ProjectPhase faza = new ProjectPhase();
            faza.setProject(project);
            faza.setNaziv(dto.naziv);
            faza.setCiljevi(dto.ciljevi);
            faza.setRokPocetak(LocalDate.parse(dto.rokPocetak));
            faza.setRokKraj(LocalDate.parse(dto.rokKraj));
            faza.setBrojVolontera(dto.brojVolontera);
            faza.setRedosled(dto.redosled);

            if (dto.potrebneVestineIds != null && !dto.potrebneVestineIds.isEmpty()) {
                List<SkillType> vestine = skillTypeRepository.findAllById(dto.potrebneVestineIds);
                faza.setPotrebneVestine(vestine);
            }

            noveFaze.add(phaseRepository.save(faza));
        }

        project.getFaze().addAll(noveFaze);
        projectRepository.save(project);

        return noveFaze.stream()
                .map(ProjectPhaseResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Vraća sve faze projekta sortirane po redosledu.
     */
    public List<ProjectPhaseResponseDTO> getFaze(Long projectId) {
        nadjiProjekat(projectId); // provjera da projekat postoji
        return phaseRepository.findByProjectIdOrderByRedosled(projectId)
                .stream()
                .map(ProjectPhaseResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Koordinator projekta postavlja pomoćne koordinatore na konkretnu fazu.
     * Više PK može nadgledati jednu fazu; nula je takođe OK.
     */
    @Transactional
    public ProjectPhaseResponseDTO postaviPomocneKoordinatoreNaFazu(Long phaseId,
                                                                     Long koordinatorId,
                                                                     List<Long> pomocniIds) {
        ProjectPhase faza = nadjisFazu(phaseId);
        provjeriVlasnistvo(faza.getProject(), koordinatorId);
        provjeriStatusOdobren(faza.getProject());

        List<Employee> pomocni = (pomocniIds == null || pomocniIds.isEmpty())
                ? new ArrayList<>()
                : pomocniIds.stream()
                        .map(this::nadjiKoordinatora)
                        .collect(Collectors.toList());

        faza.setPomocniKoordinatori(pomocni);
        return ProjectPhaseResponseDTO.from(phaseRepository.save(faza));
    }

    /**
     * Vraća sve dostupne tipove veština (za dropdown na frontendu).
     */
    public List<SkillTypeDTO> sveVestine() {
        return skillTypeRepository.findAllByOrderByNameAsc()
                .stream()
                .map(SkillTypeDTO::from)
                .collect(Collectors.toList());
    }

    // ---- Privatne pomoćne metode ----

    private void provjeriNemaPreklapanja(List<ProjectPhaseCreateDTO> faze) {
        // Sortiraj po početku, pa provjeri svaki susjedni par
        List<ProjectPhaseCreateDTO> sortirane = faze.stream()
                .sorted((a, b) -> LocalDate.parse(a.rokPocetak).compareTo(LocalDate.parse(b.rokPocetak)))
                .collect(Collectors.toList());

        for (int i = 0; i < sortirane.size() - 1; i++) {
            LocalDate krajTekuce = LocalDate.parse(sortirane.get(i).rokKraj);
            LocalDate pocetakSledece = LocalDate.parse(sortirane.get(i + 1).rokPocetak);

            if (pocetakSledece.isBefore(krajTekuce)) {
                throw new IllegalArgumentException(
                        "Faze '" + sortirane.get(i).naziv + "' i '"
                                + sortirane.get(i + 1).naziv
                                + "' se preklapaju, a koordinator je odredio da faze ne smeju da se preklapaju.");
            }
        }
    }

    private Project nadjiProjekat(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projekat sa ID=" + id + " ne postoji."));
    }

    private ProjectPhase nadjisFazu(Long id) {
        return phaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faza sa ID=" + id + " ne postoji."));
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
            throw new SecurityException("Nemate pravo da menjate ovaj projekat.");
        }
    }

    private void provjeriStatusOdobren(Project p) {
        if (p.getStatus() != ProjectStatus.ODOBREN) {
            throw new IllegalStateException(
                    "Faze se mogu postavljati samo za projekte u statusu ODOBREN.");
        }
    }

    /**
     * Koordinator označava fazu kao završenu.
     *
     * Pravilo: ako projekat NEMA dozvoljeno preklapanje faza,
     * sledeća faza po redosledu ne sme biti "aktivna" (tj. ne može
     * se krenuti sa njom dok prethodna nije završena).
     * Ova metoda samo postavlja flag — sistem na frontendu / u servisu
     * za taskove treba da proveri da li je prethodna faza završena
     * pre nego što dozvoli akcije na sledećoj fazi.
     *
     * PUT /api/faze/{phaseId}/zavrsi
     */
    @Transactional
    public ProjectPhaseResponseDTO zavrsiFazu(Long phaseId, Long koordinatorId) {
        ProjectPhase faza = nadjisFazu(phaseId);
        provjeriVlasnistvo(faza.getProject(), koordinatorId);

        if (faza.isZavrsena()) {
            throw new IllegalStateException("Faza je već označena kao završena.");
        }

        faza.setZavrsena(true);
        ProjectPhase sacuvana = phaseRepository.save(faza);

        // Ako je ovo poslednja faza projekta — projekat prelazi u ZAVRSEN automatski
        // (koordinator ga može i eksplicitno zatvoriti, ali ovo je automatski signal)
        // Ostavljamo koordinatoru da donese odluku putem
        // endpoint-a za zatvaranje projekta.

        return ProjectPhaseResponseDTO.from(sacuvana);
    }

    /**
     * Provjera da li je prethodna faza završena pre nego što se dozvole
     * akcije (kreiranje taskova, itd.) na sledećoj fazi.
     * Baca IllegalStateException ako uslov nije ispunjen.
     */
    public void provjeriMozeLiSePocetiFaza(Long phaseId) {
        ProjectPhase faza = nadjisFazu(phaseId);
        Project project = faza.getProject();

        // Ako se faze mogu preklapati — nema restrikcija
        if (project.isFazeMoguDaSePreklapaju()) {
            return;
        }

        // Nađi fazu sa manjim redosledom (prethodna faza)
        List<ProjectPhase> sveFaze = phaseRepository.findByProjectIdOrderByRedosled(project.getId());
        for (ProjectPhase prethodna : sveFaze) {
            if (prethodna.getRedosled() < faza.getRedosled() && !prethodna.isZavrsena()) {
                throw new IllegalStateException(
                        "Nije moguće raditi na fazi '" + faza.getNaziv()
                                + "' jer prethodna faza '" + prethodna.getNaziv()
                                + "' još nije završena.");
            }
        }
    }

    @Transactional
    public ProjectPhaseResponseDTO predloziNovuFazu(Long projectId,
                                                    Long koordinatorId,
                                                    ProjectPhaseCreateDTO dto,
                                                    String razlog) {
        Project project = nadjiProjekat(projectId);
        provjeriVlasnistvo(project, koordinatorId);

        if (project.getStatus() != ProjectStatus.ODOBREN) {
            throw new IllegalStateException(
                    "Nova faza se može predložiti samo za projekte u statusu ODOBREN.");
        }

        if (razlog == null || razlog.isBlank()) {
            throw new IllegalArgumentException("Razlog za dodavanje nove faze je obavezan.");
        }

        // Provjera da li su sve postojeće faze završene
        List<ProjectPhase> sveFaze = phaseRepository.findByProjectIdOrderByRedosled(projectId);
        boolean sveZavrsene = sveFaze.stream().allMatch(ProjectPhase::isZavrsena);
        if (!sveZavrsene) {
            throw new IllegalStateException(
                    "Nova faza se može dodati tek kada su sve postojeće faze završene.");
        }

        // Odredi sledeći redosled
        int sledeciRedosled = sveFaze.stream()
                .mapToInt(ProjectPhase::getRedosled)
                .max()
                .orElse(0) + 1;

        // Kreiraj fazu ali je označi kao "čeka odobrenje" (zavrsena = false, ali status projekta se menja)
        ProjectPhase novaFaza = new ProjectPhase();
        novaFaza.setProject(project);
        novaFaza.setNaziv(dto.naziv);
        novaFaza.setCiljevi(dto.ciljevi);
        novaFaza.setRokPocetak(LocalDate.parse(dto.rokPocetak));
        novaFaza.setRokKraj(LocalDate.parse(dto.rokKraj));
        novaFaza.setBrojVolontera(dto.brojVolontera);
        novaFaza.setRedosled(sledeciRedosled);

        if (dto.potrebneVestineIds != null && !dto.potrebneVestineIds.isEmpty()) {
            novaFaza.setPotrebneVestine(skillTypeRepository.findAllById(dto.potrebneVestineIds));
        }

        ProjectPhase sacuvanaFaza = phaseRepository.save(novaFaza);

        // Projekat čeka odobrenje nove faze od menadžera
        project.setRazlog(razlog);
        project.setStatus(ProjectStatus.CEKA_ODOBRENJE_NOVE_FAZE);
        projectRepository.save(project);

        return ProjectPhaseResponseDTO.from(sacuvanaFaza);
    }

    /**
     * Menadžer odobrava ili odbija novu fazu projekta.
     * Ako odobri → projekat ostaje/vraća se u ODOBREN.
     * Ako odbije → projekat se vraća u ODOBREN, nova faza se briše.
     *
     * PUT /api/projekti/{id}/nova-faza/odluka
     * Body: { "odobri": true/false, "razlog": "..." }
     */
    @Transactional
    public ProjectPhaseResponseDTO odluciONovajFazi(Long projectId, boolean odobri, String razlog) {
        Project project = nadjiProjekat(projectId);

        if (project.getStatus() != ProjectStatus.CEKA_ODOBRENJE_NOVE_FAZE) {
            throw new IllegalStateException(
                    "Projekat nije u statusu CEKA_ODOBRENJE_NOVE_FAZE.");
        }

        // Poslednja dodata faza je ona sa najvećim redosledom
        List<ProjectPhase> sveFaze = phaseRepository.findByProjectIdOrderByRedosled(projectId);
        if (sveFaze.isEmpty()) {
            throw new IllegalStateException("Nema faza na projektu.");
        }
        ProjectPhase novaFaza = sveFaze.get(sveFaze.size() - 1);

        if (odobri) {
            project.setStatus(ProjectStatus.ODOBREN);
            project.setRazlog(null);
            projectRepository.save(project);
            return ProjectPhaseResponseDTO.from(novaFaza);
        } else {
            // Obriši novu fazu
            phaseRepository.delete(novaFaza);
            project.setStatus(ProjectStatus.ODOBREN);
            project.setRazlog(razlog);
            projectRepository.save(project);
            return null;
        }
    }


}
