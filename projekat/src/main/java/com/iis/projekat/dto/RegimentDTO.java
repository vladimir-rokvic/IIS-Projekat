package com.iis.projekat.dto;

import com.iis.projekat.model.Certificate;
import com.iis.projekat.model.Regiment;
import com.iis.projekat.model.Volunteer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegimentDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numOfTrainees;
    private Certificate certificate;

    private VolunteerDTO trainer;
    private List<VolunteerDTO> trainees;

    public RegimentDTO() {}

    public RegimentDTO(Regiment r) {
        id = r.getId();
        name = r.getName();
        description = r.getDescription();
        startDate = r.getStartDate();
        endDate = r.getEndDate();
        numOfTrainees = r.getNumOfTrainees();
        certificate = r.getCertificate();

        trainer = new VolunteerDTO(r.getTrainer());

        List<VolunteerDTO> traineesDto = new ArrayList<>();
        for(Volunteer v: r.getTrainees()) {
            traineesDto.add(new VolunteerDTO(v));
        }

        trainees = traineesDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public VolunteerDTO getTrainer() {
        return trainer;
    }

    public void setTrainer(VolunteerDTO trainer) {
        this.trainer = trainer;
    }

    public List<VolunteerDTO> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<VolunteerDTO> trainees) {
        this.trainees = trainees;
    }

    public int getNumOfTrainees() {
        return numOfTrainees;
    }

    public void setNumOfTrainees(int numOfTrainees) {
        this.numOfTrainees = numOfTrainees;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}