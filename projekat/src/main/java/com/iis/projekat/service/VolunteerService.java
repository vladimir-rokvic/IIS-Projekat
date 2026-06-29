package com.iis.projekat.service;

import com.iis.projekat.dto.*;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VolunteerService {
    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SkillTypeRepository skillTypeRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    public VolunteerService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = new RestTemplate();
    }

    public boolean saveVolunteer(VolunteerUpdateDTO dto) {
        if(volunteerRepository.existsByEmail(dto.getEmail())) {
            return false;
        }

        Volunteer v = new Volunteer();
        Address a = null;

        if(addressRepository.existsByCityAndStreetAndCountry(
                dto.getCity(),
                dto.getStreet(),
                dto.getCountry()
        )) {
            a = addressRepository.findByCityAndStreetAndCountry(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );
        } else {
            a = new Address(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );

            addressRepository.save(a);
        }

        List<Availability> defaultA = new ArrayList<>();
        for(WeekDays wd : WeekDays.values()) {
            Availability na = new Availability();
            na.setDay(wd);
            na.setStartHour(9);
            na.setStartHour(17);
            na.setEnabled(false);
            na.setVolunteer(v);
            defaultA.add(na);
        }
        v.setAvailabilities(defaultA);

        v.setAddress(a);
        v.setName(dto.getName());
        v.setSurname(dto.getSurname());
        v.setPassword(passwordEncoder.encode(dto.getPassword()));
        v.setDateOfBirth(dto.getDob());
        v.setEmail(dto.getEmail());
        v.setPhone(dto.getPhone());

        volunteerRepository.save(v);
        return true;
    }

    public boolean updateVolunteer(Long id, VolunteerUpdateDTO dto) {
        Optional<Volunteer> v = volunteerRepository.findById(id);
        if(v.isEmpty()) return false;

        Volunteer oldVolunteer = v.get();
        Address a = null;

        if(addressRepository.existsByCityAndStreetAndCountry(
                dto.getCity(),
                dto.getStreet(),
                dto.getCountry()
        )) {
            a = addressRepository.findByCityAndStreetAndCountry(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );
        } else {
            a = new Address(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );

            addressRepository.save(a);
        }
        oldVolunteer.setPhone(dto.getPhone());
        oldVolunteer.setEmail(dto.getEmail());
        oldVolunteer.setDateOfBirth(dto.getDob());
        oldVolunteer.setName(dto.getName());
        oldVolunteer.setSurname(dto.getSurname());
        oldVolunteer.setAddress(a);
        oldVolunteer.setBio(dto.getBio());
        if(dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            oldVolunteer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if(dto.getSkillTypes() != null) {
            List<SkillType> skillTypes = new ArrayList<>();
            for(SkillTypeDTO s: dto.getSkillTypes()) {
                SkillType sk = skillTypeRepository.findById(s.id).orElse(null);
                skillTypes.add(sk);
            }
            oldVolunteer.setVolunteerSkillTypes(skillTypes);
        }

        if (dto.getSkills() != null) {
            Set<Skill> skills = dto.getSkills().stream().map(skillDTO -> {
                Skill s = new Skill();
                s.setName(skillDTO.getName());
                s.setDescription(skillDTO.getDesc());
                return s;
            }).collect(Collectors.toSet());
            oldVolunteer.setSkills(skills);
        }

        volunteerRepository.save(oldVolunteer);
        return true;
    }

    public List<VolunteerDTO> getAll() {
        List<VolunteerDTO> ret = new ArrayList<>();
        for(Volunteer v: volunteerRepository.findAll()) {
            ret.add(new VolunteerDTO(v));
        }
        return ret;
    }

    public VolunteerDTO getVolunteerById(Long id) {
        Volunteer v = volunteerRepository.findById(id).orElseThrow();
        return new VolunteerDTO(v);
    }

    public List<VolunteerDTO> rank(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if(task == null) return null;

        List<Volunteer> volunteers = volunteerRepository.findAll();

        List<String> taskSkillTypes = new ArrayList<>();
        for(SkillType st: task.getRequiredSkillTypes()){
            taskSkillTypes.add(st.getName());
        }

        List<VolunteerPredictDTO> volunteerPredictDTOS = new ArrayList<>();
        for(Volunteer v: volunteers) {
            VolunteerPredictDTO dto = new VolunteerPredictDTO();

            dto.setVolunteerId(v.getId());
            Double avgGrade =
                    performanceRepository.findAverageGradeByVolunteerId(v.getId());
            if(avgGrade == null)
                dto.setAvgGrade(3.0);
            else
                dto.setAvgGrade(avgGrade);

            List<String> skills = new ArrayList<>();
            for(Skill s: v.getSkills()){
                skills.add(s.getName());
            }
            dto.setVolunteerSkills(skills);

            List<String> skillTypes = new ArrayList<>();
            for(SkillType st: v.getVolunteerSkillTypes()) {
                skillTypes.add(st.getName());
            }
            dto.setVolunteerSkillTypes(skillTypes);

            volunteerPredictDTOS.add(dto);
        }

        PredictRequestDTO req = new PredictRequestDTO();
        req.setTaskSkillTypes(taskSkillTypes);
        req.setVolunteers(volunteerPredictDTOS);

        PredictionResponseDTO response = restTemplate.postForObject(
                "http://localhost:8000/model/predict",
                req,
                PredictionResponseDTO.class
        );

        List<VolunteerDTO> ret = new ArrayList<>();

        for(Volunteer v: volunteers) {
            VolunteerDTO vdto = new VolunteerDTO(v);

            for(PredictionDTO p: response.getPredictions()) {
                if (p.getVolunteerId().equals(v.getId())) {
                    vdto.setPredictedGrade(p.getPredictedRating());
                    break;
                }
            }

            ret.add(vdto);
        }

        return ret;
    }

    public void deleteVolunteer(Long id) {
        volunteerRepository.delete(volunteerRepository.getReferenceById(id));
    }

    public List<Availability> saveAvailability(List<AvailabilityDTO> availabilities) {
        List<Availability> oldAvailabilities =
                availabilityRepository.findAllByVolunteerId(availabilities.get(0).getVolunteerId());

        if(oldAvailabilities.isEmpty()) {
            Volunteer v = volunteerRepository.findById(
                    availabilities.get(0).getVolunteerId()
            ).orElse(null);
            if(v == null) return null;


            List<Availability> defaultA = new ArrayList<>();
            for(AvailabilityDTO dto : availabilities) {
                Availability na = new Availability();
                na.setDay(dto.getDay());
                na.setStartHour(dto.getStartHour());
                na.setEndHour(dto.getEndHour());
                na.setEnabled(dto.isEnabled());
                na.setVolunteer(v);
                defaultA.add(na);
            }
            availabilityRepository.saveAll(defaultA);
            return defaultA;
        }

        for(AvailabilityDTO dto : availabilities) {
            boolean saved = false;
            for(int i=0; i<oldAvailabilities.size(); ++i) {
                Availability a = oldAvailabilities.get(i);
                if(a.getDay() == dto.getDay()) {
                    a.setEnabled(dto.isEnabled());
                    a.setStartHour(dto.getStartHour());
                    a.setEndHour(dto.getEndHour());
                    availabilityRepository.save(a);
                    saved = true;
                    break;
                }
            }

            if(!saved) {
                Availability na = new Availability();
                na.setEndHour(dto.getEndHour());
                na.setStartHour(dto.getStartHour());
                na.setEnabled(dto.isEnabled());
                na.setDay(dto.getDay());
                Volunteer v = volunteerRepository.findById(dto.getVolunteerId()).orElse(null);
                if(v == null) return null;
                na.setVolunteer(v);
                availabilityRepository.save(na);
            }
        }

        return oldAvailabilities;
    }

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public void saveImage(Long id, MultipartFile file) throws IOException {
        Volunteer v = volunteerRepository.findById(id).orElseThrow();
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf('.'));

        String fileName = UUID.randomUUID() + extension;

        Path path = Paths.get(UPLOAD_DIR, fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        v.setProfileImgPath(fileName);
        volunteerRepository.save(v);
    }



    //Ajoj ovo ce da bude dugacko
    //Odvojena sekcija jer se tako osecam

    private final DateTimeFormatter DATE_FTM =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Color COLOR_HEADER_BG  = new Color(46, 125, 50);
    private static final Color COLOR_SECTION_BG = new Color(232, 245, 233);
    private static final Color COLOR_ROW_ALT = new Color(245, 253, 240);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_GRAY = Color.GRAY;

    private static Font fontTitle() {return new Font(Font.HELVETICA, 20, Font.BOLD, COLOR_WHITE);}
    private static Font fontSection() { return new Font(Font.HELVETICA, 12, Font.BOLD,   COLOR_HEADER_BG); }
    private static Font fontLabel() { return new Font(Font.HELVETICA, 10, Font.BOLD,   COLOR_TEXT); }
    private static Font fontValue() { return new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_TEXT); }
    private static Font fontSmall() { return new Font(Font.HELVETICA,  8, Font.NORMAL, COLOR_GRAY); }
    private static Font fontTableHdr(){ return new Font(Font.HELVETICA, 10, Font.BOLD,   COLOR_WHITE); }
    private static Font fontTableRow(){ return new Font(Font.HELVETICA,  9, Font.NORMAL, COLOR_TEXT); }

    public byte[] generateReport(Long id) throws IOException {
        Volunteer v = volunteerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No no"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.addTitle("Report for volunteer: " + v.getName() + " " + v.getSurname());
        document.addAuthor("Subsystem for volunteer managment");
        document.addCreationDate();

        document.open();

        addTitle(document, v);
        addBasicInfo(document, v);
        addCompletedTasks(document, v);
        addGrades(document, v);
        addPersonalStatistics(document, v);

        document.close();
        return baos.toByteArray();
    }

    private void addTitle(Document document, Volunteer v) {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(20);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_HEADER_BG);
        cell.setPadding(20);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph title = new Paragraph("Volunteer report", fontTitle());
        title.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(title);

        Paragraph volunteerName = new Paragraph(v.getName() + " " + v.getSurname(),
                new Font(Font.HELVETICA, 14, Font.BOLD, new Color(173, 216, 173)));

        volunteerName.setAlignment(Element.ALIGN_CENTER);
        volunteerName.setSpacingBefore(6);
        cell.addElement(volunteerName);

        Paragraph date = new Paragraph("Generated " + LocalDate.now().format(DATE_FTM),
                new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(200, 240, 220)));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingBefore(4);
        cell.addElement(date);

        header.addCell(cell);
        document.add(header);
    }

    private void addBasicInfo(Document document, Volunteer v) {
        addSectionHeader(document, "1. BASIC INFORMATION");

        PdfPTable layout = new PdfPTable(new float[]{1f, 2f});
        layout.setWidthPercentage(100);
        layout.setSpacingAfter(10);

        // --- LEFT: profile image ---
        PdfPCell imgCell = new PdfPCell();
        imgCell.setBorder(Rectangle.NO_BORDER);
        imgCell.setPadding(6);
        imgCell.setVerticalAlignment(Element.ALIGN_TOP);

        if (v.getProfileImgPath() != null && !v.getProfileImgPath().isBlank()) {
            try {
                Image img = Image.getInstance(UPLOAD_DIR + v.getProfileImgPath());
                img.scaleToFit(150, 150);
                img.setRotationDegrees(270);
                imgCell.addElement(img);
            } catch (Exception e) {
                Paragraph noImg = new Paragraph("No photo\navailable", fontSmall());
                noImg.setAlignment(Element.ALIGN_CENTER);
                imgCell.addElement(noImg);
            }
        } else {
            Paragraph noImg = new Paragraph("No photo\navailable", fontSmall());
            noImg.setAlignment(Element.ALIGN_CENTER);
            imgCell.addElement(noImg);
        }

        layout.addCell(imgCell);

        // --- RIGHT: info table nested inside the right cell ---
        PdfPTable infoTable = newTable(new float[]{1f, 1.6f});

        String fullAddress = "–";
        if (v.getAddress() != null) {
            Address a = v.getAddress();
            fullAddress = Stream.of(a.getStreet(), a.getCity(), a.getCountry())
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(", "));
            if (fullAddress.isBlank()) fullAddress = "–";
        }

        String dob = fmt(v.getDateOfBirth());

        String[][] rows = {
                {"Name",          nvl(v.getName())},
                {"Surname",       nvl(v.getSurname())},
                {"Email",         nvl(v.getEmail())},
                {"Phone",         nvl(v.getPhone())},
                {"Date of birth", dob},
                {"Address",       fullAddress},
        };

        for (int i = 0; i < rows.length; i++) {
            addRow(infoTable, rows[i][0], rows[i][1], i % 2 == 1);
        }

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(4);
        rightCell.addElement(infoTable);
        layout.addCell(rightCell);

        document.add(layout);

        addTextBlock(document, "Bio", v.getBio());

        if (v.getSkills() != null && !v.getSkills().isEmpty()) {
            PdfPTable skillTable = newTable(new float[]{1.5f, 3f});
            addCell(skillTable, "Skill",        fontTableHdr(), COLOR_HEADER_BG, 7);
            addCell(skillTable, "Description",  fontTableHdr(), COLOR_HEADER_BG, 7);

            int i = 0;
            for (Skill skill : v.getSkills()) {
                Color bg = (i % 2 == 1) ? COLOR_ROW_ALT : COLOR_WHITE;
                addCell(skillTable, nvl(skill.getName()),        fontTableRow(), bg, 6);
                addCell(skillTable, nvl(skill.getDescription()), fontTableRow(), bg, 6);
                i++;
            }

            document.add(skillTable);
        }

        if (v.getVolunteerSkillTypes() != null && !v.getVolunteerSkillTypes().isEmpty()) {
            PdfPTable stTable = newTable(new float[]{1.5f, 3f});
            addCell(stTable, "Type",        fontTableHdr(), COLOR_HEADER_BG, 7);
            addCell(stTable, "Description", fontTableHdr(), COLOR_HEADER_BG, 7);

            int i = 0;
            for (SkillType st : v.getVolunteerSkillTypes()) {
                Color bg = (i % 2 == 1) ? COLOR_ROW_ALT : COLOR_WHITE;
                addCell(stTable, nvl(st.getName()),        fontTableRow(), bg, 6);
                addCell(stTable, nvl(st.getDescription()), fontTableRow(), bg, 6);
                i++;
            }

            document.add(stTable);
        }
    }

    private void addCompletedTasks(Document document, Volunteer v) {
        addSectionHeader(document, "2. RECENT COMPLETED TASKS");
        List<Task> tasks = taskRepository.findRecentByVolunteerId(v.getId(), LocalDate.now());

        if (tasks.isEmpty()) {
            Paragraph none = new Paragraph("No completed tasks found.", fontValue());
            none.setSpacingBefore(6);
            document.add(none);
            return;
        }

        List<Task> recent = tasks.stream().limit(3).toList();

        for (int t = 0; t < recent.size(); t++) {
            Task task = recent.get(t);

            if (t > 0) {
                Paragraph spacer = new Paragraph(" ");
                spacer.setSpacingBefore(6);
                document.add(spacer);
            }

            String skills = (task.getRequiredSkills() == null || task.getRequiredSkills().isEmpty())
                    ? "–"
                    : task.getRequiredSkills().stream()
                    .map(Skill::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));

            String skillTypes = (task.getRequiredSkillTypes() == null || task.getRequiredSkillTypes().isEmpty())
                    ? "–"
                    : task.getRequiredSkillTypes().stream()
                    .map(SkillType::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));

            String coordinator = "–";
            if (task.getCoordinator() != null) {
                coordinator = nvl(task.getCoordinator().getName()) + " " + nvl(task.getCoordinator().getSurname());
            }

            PdfPTable table = newTable(new float[]{1f, 1.6f});

            String[][] rows = {
                    {"Name",         nvl(task.getName())},
                    {"Description",  nvl(task.getDescription())},
                    {"Start date",   fmt(task.getStartDate())},
                    {"End date",     fmt(task.getEndDate())},
                    {"Coordinator",  coordinator},
                    {"Skills",       skills},
                    {"Skill types",  skillTypes},
            };

            for (int i = 0; i < rows.length; i++) {
                addRow(table, rows[i][0], rows[i][1], i % 2 == 1);
            }

            document.add(table);
        }
    }

    private void addGrades(Document document, Volunteer v) {
        addSectionHeader(document, "3. GRADES");

        List<Performance> performances = performanceRepository
                .findByRatedVolunteerId(v.getId());
        //grade, comment, coordinator name, task name
        PdfPTable t = newTable(new float[]{1f, 1.5f, 1.6f, 1.5f});
        addCell(t, "Grade", fontTableHdr(), COLOR_HEADER_BG, 7);
        addCell(t, "Comment", fontTableHdr(), COLOR_HEADER_BG, 7);
        addCell(t, "Coordinator", fontTableHdr(), COLOR_HEADER_BG, 7);
        addCell(t, "Task", fontTableHdr(), COLOR_HEADER_BG, 7);

        int i = 0;
        for(Performance p: performances) {
            String coordinator = p.getTask().getCoordinator().getName();
            coordinator += p.getTask().getCoordinator().getSurname();
            Color bg = i%2 == 0 ? COLOR_ROW_ALT : COLOR_WHITE;
            addCell(t, p.getGrade().toString(), fontTableRow(), bg, 6);
            addCell(t, p.getComment(), fontTableRow(), bg, 6);
            addCell(t, coordinator, fontTableRow(), bg, 6);
            addCell(t, p.getTask().getName(), fontTableRow(), bg, 6);
            i++;
        }

        document.add(t);
    }

    private void addPersonalStatistics(Document document, Volunteer v) throws IOException {
        addSectionHeader(document, "4. PERSONAL STATISTICS");
        double avgGrade = performanceRepository
                .findAverageGradeByVolunteerId(v.getId());
        int numGrades = performanceRepository
                .countGradesByVolunteerId(v.getId());
        int numTasks = taskRepository
                .countTasksByVolunteerId(v.getId());
        //Weekly available hours
        List<Availability> availabilities = availabilityRepository
                .findAllByVolunteerId(v.getId());
        List<Integer> numHours = new ArrayList<>();
        for(Availability a: availabilities) {
            numHours.add(a.getEndHour() - a.getStartHour());
        }

        double avgNumHours = 0;
        for(Integer i: numHours) {
            avgNumHours += i;
        }
        avgNumHours /= numHours.size();

        String hoursWorked = v.getHoursWorked() != null ? v.getHoursWorked() + " h" : "–";
        String avgGradeStr = numGrades > 0 ? String.format("%.2f / 5.00", avgGrade) : "–";
        String avgHoursStr = availabilities.isEmpty() ? "–" : String.format("%.1f h", avgNumHours);

        PdfPTable layout = new PdfPTable(new float[]{1f, 2f});
        layout.setWidthPercentage(100);
        layout.setSpacingAfter(10);

        DefaultPieDataset dataset = new DefaultPieDataset();
        double filled = numGrades > 0 ? Math.min(avgGrade, 5.0) : 0;
        dataset.setValue("Grade", filled);
        dataset.setValue("Remaining", 5.0 - filled);

        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                false,
                false,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Grade",     new Color(255, 153, 51));
        plot.setSectionPaint("Remaining", new Color(210, 210, 210));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelGenerator(null);
        chart.setBackgroundPaint(Color.WHITE);

        BufferedImage chartImage = chart.createBufferedImage(200, 200);
        ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
        ImageIO.write(chartImage, "png", chartBaos);
        Image pieImage = Image.getInstance(chartBaos.toByteArray());

        PdfPCell pieCell = new PdfPCell();
        pieCell.setBorder(Rectangle.NO_BORDER);
        pieCell.setPadding(6);
        pieCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pieCell.addElement(pieImage);

        layout.addCell(pieCell);

        PdfPTable statsTable = newTable(new float[]{1f, 1.6f});

        String[][] rows = {
                {"Average grade",       avgGradeStr},
                {"Number of grades",    String.valueOf(numGrades)},
                {"Tasks completed",     String.valueOf(numTasks)},
                {"Hours worked",        hoursWorked},
                {"Avg. weekly hours",   avgHoursStr},
        };

        for (int i = 0; i < rows.length; i++) {
            addRow(statsTable, rows[i][0], rows[i][1], i % 2 == 1);
        }

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(4);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell.addElement(statsTable);
        layout.addCell(rightCell);

        document.add(layout);
    }

    private void addSectionHeader(Document document, String text) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(14);
        t.setSpacingAfter(6);

        PdfPCell c = new PdfPCell(new Phrase(text, fontSection()));
        c.setBackgroundColor(COLOR_SECTION_BG);
        c.setPadding(8);
        c.setBorderColor(COLOR_HEADER_BG);
        c.setBorderWidth(1);
        t.addCell(c);
        document.add(t);
    }

    private PdfPTable newTable(float[] widths) {
        PdfPTable t = new PdfPTable(widths);
        t.setWidthPercentage(100);
        return t;
    }

    private void addRow(PdfPTable table, String label, String value, boolean alt) {
        Color bg = alt ? COLOR_ROW_ALT : COLOR_WHITE;

        PdfPCell lCell = new PdfPCell(new Phrase(label, fontLabel()));
        lCell.setBackgroundColor(bg);
        lCell.setPadding(6);
        lCell.setBorderColor(new Color(210, 218, 227));
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value != null ? value : "–", fontValue()));
        vCell.setBackgroundColor(bg);
        vCell.setPadding(6);
        vCell.setBorderColor(new Color(210, 218, 227));
        table.addCell(vCell);
    }

    private void addCell(PdfPTable t, String text, Font font, Color bg, float padding) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setPadding(padding);
        c.setBorderColor(new Color(210, 218, 227));
        t.addCell(c);
    }

    private void addTextBlock(Document document, String title, String text) {
        if (text == null || text.isBlank()) return;

        Paragraph p = new Paragraph();
        p.setSpacingBefore(8);
        p.add(new Chunk(title + ": ", fontLabel()));
        p.add(new Chunk(text, fontValue()));
        document.add(p);
    }

    private String fmt(java.time.LocalDate d) {
        return d != null ? d.format(DATE_FTM) : "–";
    }

    private String nvl(String s) {
        return (s != null && !s.isBlank()) ? s : "–";
    }

}
