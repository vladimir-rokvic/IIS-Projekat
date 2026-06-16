package com.iis.projekat.dto;

import com.iis.projekat.model.Project;
import com.iis.projekat.model.ProjectStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO koji se vraća klijentu — ne sadrži binarne podatke dokumenta.
 * Za preuzimanje dokumenta postoji poseban endpoint.
 */
public class ProjectResponseDTO {

    public Long id;
    public String naziv;
    public String opis;
    public String ciljevi;
    public LocalDate rokPocetak;
    public LocalDate rokKraj;
    public ProjectStatus status;

    public Long koordinatorId;
    public String koordinatorIme;
    public String koordinatorPrezime;

    public String ciljnaGrupa;
    public String geografskaLokacija;
    public String izvoriFinansiranja;


    // Pomoćni koordinatori — lista ID-eva
    public List<Long> pomocniKoordinatoriIds;

    public String razlog;

    public boolean fazeMoguDaSePreklapaju;
    public List<ProjectPhaseResponseDTO> faze;

    public static ProjectResponseDTO from(Project p) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.id = p.getId();
        dto.naziv = p.getNaziv();
        dto.opis = p.getOpis();
        dto.ciljevi = p.getCiljevi();
        dto.rokPocetak = p.getRokPocetak();
        dto.rokKraj = p.getRokKraj();
        dto.status = p.getStatus();
        dto.koordinatorId = p.getKoordinator().getId();
        dto.koordinatorIme = p.getKoordinator().getName();
        dto.koordinatorPrezime = p.getKoordinator().getSurname();
        dto.ciljnaGrupa = p.getCiljnaGrupa();
        dto.geografskaLokacija = p.getGeografskaLokacija();
        dto.izvoriFinansiranja = p.getIzvoriFinansiranja();
        dto.razlog = p.getRazlog();
        dto.pomocniKoordinatoriIds = p.getPomocniKoordinatori()
                .stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
        dto.fazeMoguDaSePreklapaju = p.isFazeMoguDaSePreklapaju();
        dto.faze = p.getFaze().stream()
                .map(ProjectPhaseResponseDTO::from)
                .collect(Collectors.toList());
        return dto;
    }
}
