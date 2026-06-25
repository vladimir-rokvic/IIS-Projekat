package com.iis.projekat.service;

import com.iis.projekat.dto.FundingRequestCreateDTO;
import com.iis.projekat.dto.FundingRequestResponseDTO;
import com.iis.projekat.dto.ManagerFundingDecisionDTO;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectFundingRequestService {

    private final ProjectFundingRequestRepository fundingRequestRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganizationFundsRepository organizationFundsRepository;
    private final ProjectResourceRepository projectResourceRepository;

    public ProjectFundingRequestService(
            ProjectFundingRequestRepository fundingRequestRepository,
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository,
            OrganizationFundsRepository organizationFundsRepository,
            ProjectResourceRepository projectResourceRepository) {
        this.fundingRequestRepository = fundingRequestRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.organizationFundsRepository = organizationFundsRepository;
        this.projectResourceRepository = projectResourceRepository;
    }

    /**
     * Koordinator šalje zahtev za dodatna sredstva iz opštih donacija.
     */
    @Transactional
    public FundingRequestResponseDTO posaljiZahtev(Long koordinatorId, FundingRequestCreateDTO dto) {
        Employee koordinator = nadjiKoordinatora(koordinatorId);
        Project p = nadjiProjekat(dto.projectId);
        provjeriPristupKoordinatora(p, koordinatorId);

        if (dto.zahtevanIznos == null || dto.zahtevanIznos <= 0) {
            throw new IllegalArgumentException("Traženi iznos mora biti pozitivan broj.");
        }
        if (dto.razlogZahteva == null || dto.razlogZahteva.isBlank()) {
            throw new IllegalArgumentException("Razlog zahteva je obavezan.");
        }

        ProjectFundingRequest zahtev = new ProjectFundingRequest();
        zahtev.setProject(p);
        zahtev.setKoordinator(koordinator);
        zahtev.setZahtevanIznos(dto.zahtevanIznos);
        zahtev.setRazlogZahteva(dto.razlogZahteva);
        zahtev.setStatus(FundingRequestStatus.NA_CEKANJU);
        zahtev.setDatumZahteva(LocalDateTime.now());

        return FundingRequestResponseDTO.from(fundingRequestRepository.save(zahtev));
    }

    /**
     * Koordinator vidi sve zahteve koje je podneo (za sve svoje projekte).
     */
    public List<FundingRequestResponseDTO> zahteviKoordinatora(Long koordinatorId) {
        return fundingRequestRepository.findByKoordinatorId(koordinatorId)
                .stream()
                .map(FundingRequestResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Menadžer vidi zahteve za konkretan projekat (bez provere vlasništva).
     */
    public List<FundingRequestResponseDTO> zahteviZaProjekatBezProvere(Long projectId) {
        double opstaFunds = getOpshtaFunds();
        return fundingRequestRepository.findByProjectId(projectId)
                .stream()
                .map(z -> {
                    FundingRequestResponseDTO dto = FundingRequestResponseDTO.from(z);
                    dto.trenutnoOpstiIznos = opstaFunds;
                    dto.preostaloNakonOdobrenja = opstaFunds - z.getZahtevanIznos();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Koordinator vidi zahteve za konkretan projekat.
     */
    public List<FundingRequestResponseDTO> zahteviZaProjekat(Long projectId, Long koordinatorId) {
        Project p = nadjiProjekat(projectId);
        provjeriPristupKoordinatora(p, koordinatorId);
        return fundingRequestRepository.findByProjectId(projectId)
                .stream()
                .map(FundingRequestResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Menadžer vidi sve zahteve NA_CEKANJU — sa trenutnim stanjem opštih sredstava
     * i iznosom koji bi ostao ako bi svaki zahtev bio odobren.
     */
    public List<FundingRequestResponseDTO> sviZahteviNaCekanju() {
        double opstaFunds = getOpshtaFunds();
        return fundingRequestRepository.findByStatus(FundingRequestStatus.NA_CEKANJU)
                .stream()
                .map(z -> {
                    FundingRequestResponseDTO dto = FundingRequestResponseDTO.from(z);
                    dto.trenutnoOpstiIznos = opstaFunds;
                    dto.preostaloNakonOdobrenja = opstaFunds - z.getZahtevanIznos();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Menadžer vidi sve zahteve (bez obzira na status).
     */
    public List<FundingRequestResponseDTO> sviZahtevi() {
        double opstaFunds = getOpshtaFunds();
        return fundingRequestRepository.findAll()
                .stream()
                .map(z -> {
                    FundingRequestResponseDTO dto = FundingRequestResponseDTO.from(z);
                    dto.trenutnoOpstiIznos = opstaFunds;
                    dto.preostaloNakonOdobrenja = opstaFunds - z.getZahtevanIznos();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Menadžer donosi odluku o zahtevu za sredstvima.
     *
     * <ul>
     *   <li>ODOBREN — odobrava ceo traženi iznos; sredstva se oduzimaju iz opštih i
     *       dodaju na resurse projekta (ili se kreira novi resurs)</li>
     *   <li>DELIMICNO_ODOBREN — odobrava deo iznosa; odobrenIznos i razlogOdluke obavezni</li>
     *   <li>ODBIJEN — odbija zahtev; razlogOdluke obavezan</li>
     * </ul>
     */
    @Transactional
    public FundingRequestResponseDTO odluciOZahtevu(Long zahtevId, ManagerFundingDecisionDTO odluka) {
        ProjectFundingRequest zahtev = fundingRequestRepository.findById(zahtevId)
                .orElseThrow(() -> new IllegalArgumentException("Zahtev ID=" + zahtevId + " ne postoji."));

        if (zahtev.getStatus() != FundingRequestStatus.NA_CEKANJU) {
            throw new IllegalStateException("Zahtev je već obrađen.");
        }

        FundingRequestStatus noviStatus;
        try {
            noviStatus = FundingRequestStatus.valueOf(odluka.status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nedozvoljen status: " + odluka.status);
        }

        if (noviStatus == FundingRequestStatus.NA_CEKANJU) {
            throw new IllegalArgumentException("Status odluke ne može biti NA_CEKANJU.");
        }

        if (noviStatus == FundingRequestStatus.DELIMICNO_ODOBREN) {
            if (odluka.odobrenIznos == null || odluka.odobrenIznos <= 0) {
                throw new IllegalArgumentException("Odobren iznos mora biti pozitivan za delimično odobrenje.");
            }
            if (odluka.odobrenIznos >= zahtev.getZahtevanIznos()) {
                throw new IllegalArgumentException(
                        "Delimično odobrenje mora biti manji iznos od traženog. Za pun iznos koristite ODOBREN.");
            }
            if (odluka.razlogOdluke == null || odluka.razlogOdluke.isBlank()) {
                throw new IllegalArgumentException("Razlog odluke je obavezan za delimično odobrenje.");
            }
        }

        if (noviStatus == FundingRequestStatus.ODBIJEN) {
            if (odluka.razlogOdluke == null || odluka.razlogOdluke.isBlank()) {
                throw new IllegalArgumentException("Razlog odbijanja je obavezan.");
            }
        }

        double iznosZaDodeliti = 0.0;

        if (noviStatus == FundingRequestStatus.ODOBREN) {
            iznosZaDodeliti = zahtev.getZahtevanIznos();
            zahtev.setOdobrenIznos(iznosZaDodeliti);
        } else if (noviStatus == FundingRequestStatus.DELIMICNO_ODOBREN) {
            iznosZaDodeliti = odluka.odobrenIznos;
            zahtev.setOdobrenIznos(iznosZaDodeliti);
        }

        // Proveri da li organizacija ima dovoljno opštih sredstava
        if (iznosZaDodeliti > 0) {
            OrganizationFunds funds = getOrCreateFunds();
            if (funds.getDostupnoSredstava() < iznosZaDodeliti) {
                throw new IllegalStateException(
                        String.format("Nedovoljno opštih sredstava. Dostupno: %.2f, potrebno: %.2f",
                                funds.getDostupnoSredstava(), iznosZaDodeliti));
            }

            // Oduzmi iz opštih sredstava
            funds.setDostupnoSredstava(funds.getDostupnoSredstava() - iznosZaDodeliti);
            organizationFundsRepository.save(funds);

            // Dodaj na resurse projekta — traži postojeći "Odobrena dodatna sredstva" resurs
            // ili kreiraj novi
            Project p = zahtev.getProject();
            List<ProjectResource> resursi = projectResourceRepository.findByProjectId(p.getId());
            ProjectResource targetResurs = resursi.stream()
                    .filter(r -> r.getNaziv().startsWith("Odobrena dodatna sredstva"))
                    .findFirst()
                    .orElse(null);

            if (targetResurs == null) {
                targetResurs = new ProjectResource();
                targetResurs.setProject(p);
                targetResurs.setNaziv("Odobrena dodatna sredstva");
                targetResurs.setUkupnoSredstava(0.0);
                targetResurs.setDostupnoSredstava(0.0);
                targetResurs.setOpis("Sredstva odobrena od strane menadžera iz opštih donacija.");
            }

            targetResurs.setUkupnoSredstava(targetResurs.getUkupnoSredstava() + iznosZaDodeliti);
            targetResurs.setDostupnoSredstava(targetResurs.getDostupnoSredstava() + iznosZaDodeliti);
            projectResourceRepository.save(targetResurs);
        }

        zahtev.setStatus(noviStatus);
        zahtev.setRazlogOdluke(odluka.razlogOdluke);
        zahtev.setDatumOdluke(LocalDateTime.now());

        FundingRequestResponseDTO dto = FundingRequestResponseDTO.from(fundingRequestRepository.save(zahtev));
        dto.trenutnoOpstiIznos = getOpshtaFunds();
        return dto;
    }

    // --- Pomoćne metode ---

    private double getOpshtaFunds() {
        return organizationFundsRepository.findAll().stream()
                .mapToDouble(OrganizationFunds::getDostupnoSredstava)
                .sum();
    }

    private OrganizationFunds getOrCreateFunds() {
        return organizationFundsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    OrganizationFunds f = new OrganizationFunds();
                    f.setDostupnoSredstava(0.0);
                    return organizationFundsRepository.save(f);
                });
    }

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

    private void provjeriPristupKoordinatora(Project p, Long koordinatorId) {
        boolean jeVlasnik = p.getKoordinator().getId().equals(koordinatorId);
        boolean jePomocni = p.getPomocniKoordinatori().stream()
                .anyMatch(e -> e.getId().equals(koordinatorId));
        if (!jeVlasnik && !jePomocni) {
            throw new SecurityException("Nemate pravo pristupa ovom projektu.");
        }
    }
}
