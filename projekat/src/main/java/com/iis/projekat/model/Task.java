package com.iis.projekat.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Skill> requiredSkills;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    private Employee coordinator;

    @OneToOne(mappedBy = "task")
    private Performance performance;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "phase_id")
    private ProjectPhase phase;

    @ManyToMany
    @JoinTable(
            name = "task_skill_types",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_type_id")
    )
    private List<SkillType> requiredSkillTypes = new ArrayList<>();

    public Task() {}

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

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
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

    public Employee getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Employee coordinator) {
        this.coordinator = coordinator;
    }

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    public ProjectPhase getPhase() { return phase; }
    public void setPhase(ProjectPhase phase) { this.phase = phase; }

    public List<SkillType> getRequiredSkillTypes() {
        return requiredSkillTypes;
    }

    public void setRequiredSkillTypes(List<SkillType> requiredSkillTypes) {
        this.requiredSkillTypes = requiredSkillTypes;
    }
}
