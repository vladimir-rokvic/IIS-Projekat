package com.iis.projekat.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Regiment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numOfTrainees;

    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Volunteer trainer;

    @ManyToMany
    @JoinTable(
            name = "trainees",
            joinColumns = @JoinColumn(name = "regiment_id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id")
    )
    private List<Volunteer> trainees;

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

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Volunteer getTrainer() {
        return trainer;
    }

    public void setTrainer(Volunteer trainer) {
        this.trainer = trainer;
    }

    public List<Volunteer> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<Volunteer> trainees) {
        this.trainees = trainees;
    }

    public Integer getNumOfTrainees() {
        return numOfTrainees;
    }

    public void setNumOfTrainees(Integer numOfTrainees) {
        this.numOfTrainees = numOfTrainees;
    }
}
