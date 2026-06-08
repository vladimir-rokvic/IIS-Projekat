package com.iis.projekat.dto;

import com.iis.projekat.model.Performance;
import com.iis.projekat.model.Skill;
import com.iis.projekat.model.Task;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class TaskDTO {
    private Long id;
    private String name;
    private String description;

    private Set<SkillDTO> requiredSkills;

    private VolunteerDTO volunteer;
    private UserDTO coordinator;
    private Performance performance;

    private LocalDate startDate;
    private LocalDate endDate;

    public TaskDTO() {}

    public TaskDTO(Task t) {
        this.id = t.getId();
        this.name = t.getName();
        this.description = t.getDescription();
        this.startDate = t.getStartDate();
        this.endDate = t.getEndDate();
        if(t.getVolunteer() != null) {
            this.volunteer = new VolunteerDTO(t.getVolunteer());
        } else {
            this.volunteer = null;
        }

        Set<SkillDTO> dtos = new HashSet<>();
        for(Skill s: t.getRequiredSkills()){
            dtos.add(new SkillDTO(s));
        }

        this.requiredSkills = dtos;
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

    public Set<SkillDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<SkillDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public VolunteerDTO getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(VolunteerDTO volunteer) {
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

    public UserDTO getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(UserDTO coordinator) {
        this.coordinator = coordinator;
    }

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }
}
