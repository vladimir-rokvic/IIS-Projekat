package com.iis.projekat.dto;

import com.iis.projekat.model.ProjectResource;

public class ProjectResourceDTO {
    public Long id;
    public Long projectId;
    public String projectNaziv;
    public String naziv;
    public Double ukupnoSredstava;
    public Double dostupnoSredstava;
    public Double potrosenoSredstava;
    public String opis;

    public static ProjectResourceDTO from(ProjectResource r) {
        ProjectResourceDTO dto = new ProjectResourceDTO();
        dto.id = r.getId();
        dto.projectId = r.getProject().getId();
        dto.projectNaziv = r.getProject().getNaziv();
        dto.naziv = r.getNaziv();
        dto.ukupnoSredstava = r.getUkupnoSredstava();
        dto.dostupnoSredstava = r.getDostupnoSredstava();
        dto.potrosenoSredstava = r.getUkupnoSredstava() - r.getDostupnoSredstava();
        dto.opis = r.getOpis();
        return dto;
    }
}
