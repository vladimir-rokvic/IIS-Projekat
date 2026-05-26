package com.iis.projekat.dto;

import java.util.List;

public class UpdateTaskDTO {
    private String name;
    private String description;
    private Long volunteerId;
    private List<SkillDTO> requiredSkills;

    public UpdateTaskDTO() {}

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

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public List<SkillDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<SkillDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
