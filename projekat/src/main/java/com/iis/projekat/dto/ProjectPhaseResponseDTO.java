package com.iis.projekat.dto;

import com.iis.projekat.model.ProjectPhase;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectPhaseResponseDTO {
    public Long id;
    public Long projectId;
    public String naziv;
    public String ciljevi;
    public LocalDate rokPocetak;
    public LocalDate rokKraj;
    public Integer brojVolontera;
    public Integer redosled;

    // Veštine — vraćamo i ID i naziv radi prikaza na frontendu
    public List<SkillTypeDTO> potrebneVestine;

    // Pomoćni koordinatori ove faze
    public List<Long> pomocniKoordinatoriIds;
    public List<String> pomocniKoordinatoriImena;  // "Ime Prezime" za prikaz

    public static ProjectPhaseResponseDTO from(ProjectPhase phase) {
        ProjectPhaseResponseDTO dto = new ProjectPhaseResponseDTO();
        dto.id = phase.getId();
        dto.projectId = phase.getProject().getId();
        dto.naziv = phase.getNaziv();
        dto.ciljevi = phase.getCiljevi();
        dto.rokPocetak = phase.getRokPocetak();
        dto.rokKraj = phase.getRokKraj();
        dto.brojVolontera = phase.getBrojVolontera();
        dto.redosled = phase.getRedosled();

        dto.potrebneVestine = phase.getPotrebneVestine().stream()
                .map(SkillTypeDTO::from)
                .collect(Collectors.toList());

        dto.pomocniKoordinatoriIds = phase.getPomocniKoordinatori().stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());

        dto.pomocniKoordinatoriImena = phase.getPomocniKoordinatori().stream()
                .map(e -> e.getName() + " " + e.getSurname())
                .collect(Collectors.toList());

        return dto;
    }
}
