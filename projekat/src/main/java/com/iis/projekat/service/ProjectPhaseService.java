package com.iis.projekat.service;

import com.iis.projekat.dto.*;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectPhaseService {

    private final ProjectRepository projectRepository;
    private final ProjectPhaseRepository phaseRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillTypeRepository skillTypeRepository;
    private final VolunteerRepository volunteerRepository;
    private final TaskRepository taskRepository;

    public ProjectPhaseService(ProjectRepository projectRepository,
                               ProjectPhaseRepository phaseRepository,
                               EmployeeRepository employeeRepository,
                               SkillTypeRepository skillTypeRepository,
                               VolunteerRepository volunteerRepository,
                               TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.phaseRepository = phaseRepository;
        this.employeeRepository = employeeRepository;
        this.skillTypeRepository = skillTypeRepository;
        this.volunteerRepository = volunteerRepository;
        this.taskRepository = taskRepository;
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
                                                    ProjectPhaseCreateDTO dto) {
        Project project = nadjiProjekat(projectId);
        provjeriVlasnistvo(project, koordinatorId);

        if (project.getStatus() != ProjectStatus.ODOBREN) {
            throw new IllegalStateException(
                    "Nova faza se može predložiti samo za projekte u statusu ODOBREN.");
        }

        List<ProjectPhase> sveFaze = phaseRepository.findByProjectIdOrderByRedosled(projectId);

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

        LocalDate pocetak = LocalDate.parse(dto.rokPocetak);
        LocalDate kraj = LocalDate.parse(dto.rokKraj);

        if (pocetak.isBefore(project.getRokPocetak())
                || kraj.isAfter(project.getRokKraj())) {
            throw new IllegalArgumentException(
                    "Rokovi faze moraju biti unutar rokova projekta.");
        }

        if (!project.isFazeMoguDaSePreklapaju()) {

            for (ProjectPhase postojeca : sveFaze) {

                boolean preklapanje =
                        !kraj.isBefore(postojeca.getRokPocetak())
                                && !pocetak.isAfter(postojeca.getRokKraj());

                if (preklapanje) {
                    throw new IllegalArgumentException(
                            "Nova faza se preklapa sa fazom '" +
                                    postojeca.getNaziv() + "'");
                }
            }
        }

        novaFaza.setBrojVolontera(dto.brojVolontera);
        novaFaza.setRedosled(sledeciRedosled);

        if (dto.potrebneVestineIds != null && !dto.potrebneVestineIds.isEmpty()) {
            novaFaza.setPotrebneVestine(skillTypeRepository.findAllById(dto.potrebneVestineIds));
        }

        ProjectPhase sacuvanaFaza = phaseRepository.save(novaFaza);
        projectRepository.save(project);

        return ProjectPhaseResponseDTO.from(sacuvanaFaza);
    }

    /**
     * Preporuka volontera za fazu projekta (FR: Raspoređivanje volontera).
     *
     * Za svakog volontera se računa broj poklapanja između njegovih veština
     * (Volunteer.skills, po nazivu) i veština potrebnih za fazu (potrebneVestine).
     * Volonter je "dostupan" ako nema task čiji period [startDate, endDate]
     * preklapa period trajanja faze [rokPocetak, rokKraj].
     *
     * Vraćaju se samo volonteri koji poseduju BAREM JEDNU od traženih veština
     * i koji su dostupni, sortirani po broju poklapajućih veština (opadajuće).
     * Ako ih je manje od faza.brojVolontera, dodaje se odgovarajuća poruka,
     * ali se ipak vraća kompletna (kraća) lista.
     *
     * GET /api/faze/{phaseId}/preporuke-volontera
     */
    @Transactional(readOnly = true)
    public VolonterPreporukaResponseDTO preporuciVolontere(Long phaseId) {
        ProjectPhase faza = nadjisFazu(phaseId);

        Set<String> traziveVestine = faza.getPotrebneVestine().stream()
                .map(sv -> sv.getName().trim().toLowerCase())
                .collect(Collectors.toSet());

        int trazenBrojVolontera = faza.getBrojVolontera() != null ? faza.getBrojVolontera() : 0;

        // Volonteri koji su već dodeljeni na neki task unutar ove faze - "zakucani"
        Set<Long> zakucaniVolonterIds = taskRepository.findAllByPhaseId(phaseId).stream()
                .map(Task::getVolunteer)
                .filter(v -> v != null)
                .map(Volunteer::getId)
                .collect(Collectors.toSet());

        List<Volunteer> sviVolonteri = volunteerRepository.findAll();

        List<VolunteerRecommendationDTO> zakucani = new ArrayList<>();
        List<VolunteerRecommendationDTO> preporuceni = new ArrayList<>();

        for (Volunteer v : sviVolonteri) {
            List<String> poklapanja = new ArrayList<>();
            if (v.getSkills() != null) {
                for (Skill s : v.getSkills()) {
                    if (s.getName() != null
                            && traziveVestine.contains(s.getName().trim().toLowerCase())) {
                        poklapanja.add(s.getName());
                    }
                }
            }

            boolean jeZakucan = zakucaniVolonterIds.contains(v.getId());

            if (jeZakucan) {
                // Zakucani volonteri se uvek prikazuju, bez obzira na poklapanja/dostupnost
                zakucani.add(new VolunteerRecommendationDTO(
                        v, poklapanja.size(), traziveVestine.size(), poklapanja, true, true));
                continue;
            }

            // Samo volonteri koji poseduju bar jednu od traženih veština
            if (poklapanja.isEmpty()) {
                continue;
            }

            boolean dostupan = jeVolonterDostupan(v.getId(), faza.getRokPocetak(), faza.getRokKraj());
            if (!dostupan) {
                continue;
            }

            preporuceni.add(new VolunteerRecommendationDTO(
                    v, poklapanja.size(), traziveVestine.size(), poklapanja, true, false));
        }

        // Sortiraj zakucane po prezimenu, preporučene po broju poklapajućih veština opadajuće
        zakucani.sort(Comparator.comparing(VolunteerRecommendationDTO::getSurname));
        preporuceni.sort(
                Comparator.comparingInt(VolunteerRecommendationDTO::getMatchedSkillsCount).reversed()
                        .thenComparing(VolunteerRecommendationDTO::getSurname)
        );

        int ukupnoPronadjenih = preporuceni.size();

        String poruka = null;
        if (ukupnoPronadjenih < trazenBrojVolontera) {
            poruka = "Pronađeno je samo " + ukupnoPronadjenih + " od potrebnih "
                    + trazenBrojVolontera + " volontera koji poseduju traženu veštinu i "
                    + "slobodni su u periodu trajanja faze ("
                    + faza.getRokPocetak() + " – " + faza.getRokKraj() + ").";
        }

        // Zakucani ulaze u ukupan broj potrebnih volontera
        int slobodnihMesta = trazenBrojVolontera - zakucani.size();

        if (slobodnihMesta < 0) {
            slobodnihMesta = 0;
        }

        if (preporuceni.size() > slobodnihMesta) {
            preporuceni = preporuceni.subList(0, slobodnihMesta);
        }

        // Zakucani volonteri se prikazuju na vrhu, pa preporučeni
        List<VolunteerRecommendationDTO> rezultat = new ArrayList<>(zakucani);
        rezultat.addAll(preporuceni);

        return new VolonterPreporukaResponseDTO(rezultat, trazenBrojVolontera, poruka);
    }


    /**
     * Provjera dostupnosti volontera u periodu [pocetak, kraj]:
     * volonter je dostupan ako nema task koji se preklapa sa tim periodom.
     */
    private boolean jeVolonterDostupan(Long volunteerId, LocalDate pocetak, LocalDate kraj) {
        List<Task> preklapajuciTaskovi = taskRepository
                .findAllByVolunteerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        volunteerId, kraj, pocetak);
        return preklapajuciTaskovi.isEmpty();
    }

    /**
     * Ažurira SAMO podatke jedne postojeće faze — naziv, ciljeve, rokove,
     * broj volontera, potrebne veštine i redosled.
     * Taskovi, zavrsena flag i ID ostaju netaknuti.
     * PUT /api/faze/{id}
     */
    @Transactional
    public ProjectPhaseResponseDTO azurirajFazu(Long phaseId, Long koordinatorId,
                                                ProjectPhaseCreateDTO dto) {
        ProjectPhase faza = nadjisFazu(phaseId);
        provjeriVlasnistvo(faza.getProject(), koordinatorId);

        faza.setNaziv(dto.naziv);
        faza.setCiljevi(dto.ciljevi);
        faza.setRokPocetak(LocalDate.parse(dto.rokPocetak));
        faza.setRokKraj(LocalDate.parse(dto.rokKraj));
        faza.setBrojVolontera(dto.brojVolontera);

        if (dto.redosled != null) {
            faza.setRedosled(dto.redosled);
        }

        List<SkillType> vestine = (dto.potrebneVestineIds != null && !dto.potrebneVestineIds.isEmpty())
                ? skillTypeRepository.findAllById(dto.potrebneVestineIds)
                : new ArrayList<>();
        faza.setPotrebneVestine(vestine);

        // zavrsena, taskovi i ID se ne diraju

        return ProjectPhaseResponseDTO.from(phaseRepository.save(faza));
    }



}
