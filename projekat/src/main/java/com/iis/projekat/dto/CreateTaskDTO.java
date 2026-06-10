package com.iis.projekat.dto;

import java.time.LocalDate;
import java.util.Set;

public class CreateTaskDTO {
    private String name;
    private String description;

    private Set<SkillDTO> requiredSkills;

    private Long volunteerId;
    private Long coordinatorId;

    private LocalDate startDate;
    private LocalDate endDate;

    public CreateTaskDTO() {}

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

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
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

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }
}
