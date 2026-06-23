package com.iis.projekat.service;

import com.iis.projekat.dto.ProjectReportDTO;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.DonationRepository;
import com.iis.projekat.repository.KpiRepository;
import com.iis.projekat.repository.PerformanceRepository;
import com.iis.projekat.repository.ProjectRepository;
import com.iis.projekat.repository.TaskRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kreira PDF izveštaj o efektivnosti projekta, utrošenim sredstvima
 * i postignutim ishodima. Koristi OpenPDF biblioteku.
 */
@Service
public class ProjectReportService {

    // Formatiranje datuma
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    // Boje
    private static final Color COLOR_HEADER_BG  = new Color(30, 77, 120);   // tamno plava
    private static final Color COLOR_SECTION_BG = new Color(220, 234, 246); // svetlo plava
    private static final Color COLOR_ROW_ALT    = new Color(245, 249, 253); // gotovo bela
    private static final Color COLOR_WHITE      = Color.WHITE;
    private static final Color COLOR_TEXT       = new Color(33, 37, 41);

    // Fontovi (BaseFont.HELVETICA radi bez eksternih resursa)
    private static Font fontTitle()   { return new Font(Font.HELVETICA, 20, Font.BOLD,   Color.WHITE); }
    private static Font fontSection() { return new Font(Font.HELVETICA, 12, Font.BOLD,   COLOR_HEADER_BG); }
    private static Font fontLabel()   { return new Font(Font.HELVETICA, 10, Font.BOLD,   COLOR_TEXT); }
    private static Font fontValue()   { return new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_TEXT); }
    private static Font fontSmall()   { return new Font(Font.HELVETICA,  8, Font.NORMAL, Color.GRAY); }
    private static Font fontTableHdr(){ return new Font(Font.HELVETICA, 10, Font.BOLD,   Color.WHITE); }
    private static Font fontTableRow(){ return new Font(Font.HELVETICA,  9, Font.NORMAL, COLOR_TEXT); }

    // Repozitorijumi
    private final ProjectRepository     projectRepository;
    private final DonationRepository    donationRepository;
    private final KpiRepository         kpiRepository;
    private final TaskRepository        taskRepository;
    private final PerformanceRepository performanceRepository;

    public ProjectReportService(ProjectRepository projectRepository,
                                DonationRepository donationRepository,
                                KpiRepository kpiRepository,
                                TaskRepository taskRepository,
                                PerformanceRepository performanceRepository) {
        this.projectRepository     = projectRepository;
        this.donationRepository    = donationRepository;
        this.kpiRepository         = kpiRepository;
        this.taskRepository        = taskRepository;
        this.performanceRepository = performanceRepository;
    }

    //  Javni API

    /**
     * Generise PDF izvestaj za projekat sa datim ID-jem.
     *
     * @param projektId ID projekta
     * @return bajt-niz koji predstavlja PDF dokument
     */
    public byte[] generisiIzvestaj(Long projektId) {
        Project project = projectRepository.findById(projektId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Projekat sa ID=" + projektId + " ne postoji."));

        if (project.getStatus() != ProjectStatus.ZAVRSEN) {
            throw new IllegalStateException(
                    "Izveštaj se može generisati samo za završene projekte.");
        }

        ProjectReportDTO dto = popuniDTO(project);
        return generisiPDF(dto);
    }

    //  Punjenje DTO-a iz baze

    private ProjectReportDTO popuniDTO(Project project) {
        ProjectReportDTO dto = new ProjectReportDTO();

        // Osnovni podaci
        dto.projektId           = project.getId();
        dto.naziv               = project.getNaziv();
        dto.opis                = project.getOpis();
        dto.ciljevi             = project.getCiljevi();
        dto.rokPocetak          = project.getRokPocetak();
        dto.rokKraj             = project.getRokKraj();
        dto.status              = project.getStatus().name();
        dto.geografskaLokacija  = project.getGeografskaLokacija();
        dto.ciljnaGrupa         = project.getCiljnaGrupa();
        dto.izvoriFinansiranja  = project.getIzvoriFinansiranja();

        // Koordinatori
        Employee k = project.getKoordinator();
        dto.koordinatorIme = k.getName() + " " + k.getSurname();
        dto.pomocniKoordinatori = project.getPomocniKoordinatori().stream()
                .map(e -> e.getName() + " " + e.getSurname())
                .collect(Collectors.toList());

        // KPI
        kpiRepository.findByProjectId(project.getId()).ifPresent(kpi -> {
            dto.kpiOpis            = kpi.getOpis();
            dto.kpiIntervalMerenja = kpi.getIntervalMerenja().name();
        });

        // Finansije — donacije za ovaj projekat
        List<Donation> donacije = donationRepository.findAll().stream()
                .filter(d -> d.getProject() != null &&
                        d.getProject().getId().equals(project.getId()))
                .collect(Collectors.toList());
        dto.ukupnoDonirano = donacije.stream()
                .mapToDouble(Donation::getAmount).sum();
        dto.brojDonatora = (int) donacije.stream()
                .filter(d -> d.getDonor() != null)
                .map(d -> d.getDonor().getId())
                .distinct().count();

        // Faze
        List<ProjectPhase> faze = project.getFaze();
        dto.ukupnoFaza   = faze.size();
        dto.zavrsenihFaza = (int) faze.stream().filter(ProjectPhase::isZavrsena).count();

        dto.faze = faze.stream().map(f -> {
            ProjectReportDTO.FazaIzvestajDTO fd = new ProjectReportDTO.FazaIzvestajDTO();
            fd.naziv         = f.getNaziv();
            fd.rokPocetak    = f.getRokPocetak();
            fd.rokKraj       = f.getRokKraj();
            fd.zavrsena      = f.isZavrsena();
            fd.brojZadataka  = f.getTaskovi().size();
            fd.brojVolontera = f.getBrojVolontera();
            fd.potrebneVestine = f.getPotrebneVestine().stream()
                    .map(SkillType::getName)
                    .collect(Collectors.toList());
            return fd;
        }).collect(Collectors.toList());

        // Svi taskovi projekta
        List<Task> sviTaskovi = faze.stream()
                .flatMap(f -> f.getTaskovi().stream())
                .collect(Collectors.toList());
        dto.ukupnoZadataka  = sviTaskovi.size();
        dto.zavrseniZadaci  = (int) sviTaskovi.stream()
                .filter(t -> t.getEndDate() != null &&
                        !t.getEndDate().isAfter(java.time.LocalDate.now()))
                .count();

        // Volonteri - jedinstveni volonteri sa taskova
        Set<Long> volonterId = sviTaskovi.stream()
                .filter(t -> t.getVolunteer() != null)
                .map(t -> t.getVolunteer().getId())
                .collect(Collectors.toSet());
        dto.ukupnoVolontera = volonterId.size();

        // Prosecna ocena volontera koji su radili na projektu
        if (!volonterId.isEmpty()) {
            OptionalDouble avg = volonterId.stream()
                    .mapToDouble(vid -> {
                        Double g = performanceRepository.findAverageGradeByVolunteerId(vid);
                        return g != null ? g : 0.0;
                    })
                    .filter(g -> g > 0)
                    .average();
            dto.prosecnaOcenaVolontera = avg.isPresent()
                    ? Math.round(avg.getAsDouble() * 100.0) / 100.0
                    : null;
        }

        return dto;
    }

    //  Generisanje PDF izveštaja

    private byte[] generisiPDF(ProjectReportDTO dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // Metadata
        doc.addTitle("Izveštaj o projektu - " + dto.naziv);
        doc.addAuthor("Podsistem za upravljanje projektima");
        doc.addCreationDate();

        doc.open();

        dodajNaslov(doc, dto);
        dodajOsnovnePodatke(doc, dto);
        dodajFinansije(doc, dto);
        dodajKpi(doc, dto);
        dodajFaze(doc, dto);
        dodajStatistikuVolontera(doc, dto);
        dodajFooter(doc, writer);

        doc.close();
        return baos.toByteArray();
    }

    // Sekcije

    private void dodajNaslov(Document doc, ProjectReportDTO dto) {
        // Plava pozadinska tabla kao header
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(20);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_HEADER_BG);
        cell.setPadding(20);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph naslov = new Paragraph("IZVEŠTAJ O PROJEKTU", fontTitle());
        naslov.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(naslov);

        Paragraph imeProjekta = new Paragraph(dto.naziv,
                new Font(Font.HELVETICA, 14, Font.BOLD, new Color(173, 216, 255)));
        imeProjekta.setAlignment(Element.ALIGN_CENTER);
        imeProjekta.setSpacingBefore(6);
        cell.addElement(imeProjekta);

        Paragraph datum = new Paragraph(
                "Generisano: " + java.time.LocalDate.now().format(DATE_FMT),
                new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(200, 220, 240)));
        datum.setAlignment(Element.ALIGN_CENTER);
        datum.setSpacingBefore(4);
        cell.addElement(datum);

        header.addCell(cell);
        doc.add(header);
    }

    private void dodajOsnovnePodatke(Document doc, ProjectReportDTO dto) {
        dodajSectionHeader(doc, "1. OSNOVNI PODACI O PROJEKTU");

        PdfPTable tabla = novaTabla(new float[]{35f, 65f});
        tabla.setSpacingAfter(14);

        dodajRed(tabla, "Naziv projekta",     dto.naziv,     false);
        dodajRed(tabla, "Status",             statusSrp(dto.status), true);
        dodajRed(tabla, "Datum početka",      fmt(dto.rokPocetak),  false);
        dodajRed(tabla, "Datum kraja",        fmt(dto.rokKraj),     true);
        dodajRed(tabla, "Koordinator",        dto.koordinatorIme,   false);
        dodajRed(tabla, "Pomoćni koord.",
                dto.pomocniKoordinatori.isEmpty()
                        ? "–"
                        : String.join(", ", dto.pomocniKoordinatori), true);
        dodajRed(tabla, "Ciljna grupa",       nvl(dto.ciljnaGrupa),      false);
        dodajRed(tabla, "Lokacija",           nvl(dto.geografskaLokacija), true);
        dodajRed(tabla, "Finansiranje",       nvl(dto.izvoriFinansiranja), false);

        doc.add(tabla);

        // Opis i ciljevi - u posebnim blokovima
        dodajTextBlok(doc, "Opis projekta", dto.opis);
        dodajTextBlok(doc, "Ciljevi projekta", dto.ciljevi);
    }

    private void dodajFinansije(Document doc, ProjectReportDTO dto) {
        dodajSectionHeader(doc, "2. UTROŠENA SREDSTVA I FINANSIRANJE");

        PdfPTable tabla = novaTabla(new float[]{50f, 50f});
        tabla.setSpacingAfter(14);

        dodajRed(tabla, "Ukupno prikupljeno (donacije)",
                String.format("%.2f RSD", dto.ukupnoDonirano), false);
        dodajRed(tabla, "Broj donatora",
                String.valueOf(dto.brojDonatora), true);
        dodajRed(tabla, "Izvori finansiranja",
                nvl(dto.izvoriFinansiranja), false);

        doc.add(tabla);
    }

    private void dodajKpi(Document doc, ProjectReportDTO dto) {
        if (dto.kpiOpis == null) return;

        dodajSectionHeader(doc, "3. KLJUČNI INDIKATORI UČINKA (KPI)");

        PdfPTable tabla = novaTabla(new float[]{35f, 65f});
        tabla.setSpacingAfter(14);

        dodajRed(tabla, "Opis KPI",            dto.kpiOpis,           false);
        dodajRed(tabla, "Interval merenja",    nvl(dto.kpiIntervalMerenja), true);

        doc.add(tabla);
    }

    private void dodajFaze(Document doc, ProjectReportDTO dto) {
        dodajSectionHeader(doc, "4. FAZE PROJEKTA");

        // Sažetak
        PdfPTable sumarTable = novaTabla(new float[]{50f, 50f});
        sumarTable.setSpacingAfter(10);
        dodajRed(sumarTable, "Ukupno faza",    String.valueOf(dto.ukupnoFaza),     false);
        dodajRed(sumarTable, "Završenih faza", String.valueOf(dto.zavrsenihFaza),  true);
        dodajRed(sumarTable, "Ukupno zadataka", String.valueOf(dto.ukupnoZadataka), false);
        dodajRed(sumarTable, "Završenih zadataka", String.valueOf(dto.zavrseniZadaci), true);
        doc.add(sumarTable);

        if (dto.faze == null || dto.faze.isEmpty()) return;

        // Tabela faza
        PdfPTable tFaze = novaTabla(new float[]{22f, 15f, 15f, 12f, 12f, 24f});
        tFaze.setSpacingAfter(14);

        String[] zaglavlje = {"Naziv faze", "Početak", "Kraj", "Volonteri", "Zadaci", "Veštine"};
        for (String z : zaglavlje) {
            PdfPCell h = new PdfPCell(new Phrase(z, fontTableHdr()));
            h.setBackgroundColor(COLOR_HEADER_BG);
            h.setPadding(6);
            h.setBorderColor(COLOR_HEADER_BG);
            tFaze.addCell(h);
        }

        boolean alt = false;
        for (ProjectReportDTO.FazaIzvestajDTO f : dto.faze) {
            Color bg = alt ? COLOR_ROW_ALT : COLOR_WHITE;

            String vestine = f.potrebneVestine == null || f.potrebneVestine.isEmpty()
                    ? "–" : String.join(", ", f.potrebneVestine);
            String statusFaze = f.zavrsena ? "✓ Završena" : "U toku";

            dodajCeliju(tFaze, f.naziv + "\n" + statusFaze, fontTableRow(), bg, 6);
            dodajCeliju(tFaze, fmt(f.rokPocetak), fontTableRow(), bg, 6);
            dodajCeliju(tFaze, fmt(f.rokKraj),    fontTableRow(), bg, 6);
            dodajCeliju(tFaze, String.valueOf(f.brojVolontera), fontTableRow(), bg, 6);
            dodajCeliju(tFaze, String.valueOf(f.brojZadataka),  fontTableRow(), bg, 6);
            dodajCeliju(tFaze, vestine, fontTableRow(), bg, 6);

            alt = !alt;
        }
        doc.add(tFaze);
    }

    private void dodajStatistikuVolontera(Document doc, ProjectReportDTO dto) {
        dodajSectionHeader(doc, "5. VOLONTERI I OSTVARENI ISHODI");

        PdfPTable tabla = novaTabla(new float[]{55f, 45f});
        tabla.setSpacingAfter(14);

        dodajRed(tabla, "Ukupno angažovanih volontera",
                String.valueOf(dto.ukupnoVolontera), false);
        dodajRed(tabla, "Prosečna ocena volontera",
                dto.prosecnaOcenaVolontera != null
                        ? String.format("%.2f / 5.00", dto.prosecnaOcenaVolontera)
                        : "Nema ocena", true);
        dodajRed(tabla, "Procenat završenih zadataka",
                dto.ukupnoZadataka > 0
                        ? String.format("%.1f%%",
                        100.0 * dto.zavrseniZadaci / dto.ukupnoZadataka)
                        : "–", false);

        doc.add(tabla);
    }

    private void dodajFooter(Document doc, PdfWriter writer) {
        doc.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph(
                "Ovaj izveštaj je automatski generisan od strane sistema za upravljanje projektima.",
                fontSmall());
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    // Pomoćne metode za formatiranje

    private void dodajSectionHeader(Document doc, String tekst) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(14);
        t.setSpacingAfter(6);

        PdfPCell c = new PdfPCell(new Phrase(tekst, fontSection()));
        c.setBackgroundColor(COLOR_SECTION_BG);
        c.setPadding(8);
        c.setBorderColor(COLOR_HEADER_BG);
        c.setBorderWidth(1);
        t.addCell(c);
        doc.add(t);
    }

    private PdfPTable novaTabla(float[] sirine) {
        PdfPTable t = new PdfPTable(sirine);
        t.setWidthPercentage(100);
        return t;
    }

    private void dodajRed(PdfPTable tabla, String label, String value, boolean alt) {
        Color bg = alt ? COLOR_ROW_ALT : COLOR_WHITE;

        PdfPCell lCell = new PdfPCell(new Phrase(label, fontLabel()));
        lCell.setBackgroundColor(bg);
        lCell.setPadding(6);
        lCell.setBorderColor(new Color(210, 218, 227));
        tabla.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value != null ? value : "–", fontValue()));
        vCell.setBackgroundColor(bg);
        vCell.setPadding(6);
        vCell.setBorderColor(new Color(210, 218, 227));
        tabla.addCell(vCell);
    }

    private void dodajCeliju(PdfPTable t, String tekst, Font font, Color bg, float padding) {
        PdfPCell c = new PdfPCell(new Phrase(tekst, font));
        c.setBackgroundColor(bg);
        c.setPadding(padding);
        c.setBorderColor(new Color(210, 218, 227));
        t.addCell(c);
    }

    private void dodajTextBlok(Document doc, String naslov, String tekst) {
        if (tekst == null || tekst.isBlank()) return;

        Paragraph p = new Paragraph();
        p.setSpacingBefore(8);
        p.add(new Chunk(naslov + ": ", fontLabel()));
        p.add(new Chunk(tekst, fontValue()));
        doc.add(p);
    }

    private String fmt(java.time.LocalDate d) {
        return d != null ? d.format(DATE_FMT) : "–";
    }

    private String nvl(String s) {
        return (s != null && !s.isBlank()) ? s : "–";
    }

    private String statusSrp(String status) {
        if (status == null) return "–";
        return switch (status) {
            case "ZAVRSEN"            -> "Završen";
            case "ODOBREN"            -> "Odobren";
            case "U_PRIPREMI"         -> "U pripremi";
            case "SPREMAN_ZA_ODOBRENJE" -> "Spreman za odobrenje";
            case "NEOPHODNA_IZMENA"   -> "Neophodna izmena";
            case "ODBIJEN"            -> "Odbijen";
            default -> status;
        };
    }
}

