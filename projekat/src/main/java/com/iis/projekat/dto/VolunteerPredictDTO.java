package com.iis.projekat.dto;

import java.util.List;

public class VolunteerPredictDTO {
    private Long volunteerId;
    private Double avgGrade;
    private List<String> volunteerSkills;
    private List<String> volunteerSkillTypes;

    public VolunteerPredictDTO() {}

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Double getAvgGrade() {
        return avgGrade;
    }

    public void setAvgGrade(Double avgGrade) {
        this.avgGrade = avgGrade;
    }

    public List<String> getVolunteerSkills() {
        return volunteerSkills;
    }

    public void setVolunteerSkills(List<String> volunteerSkills) {
        this.volunteerSkills = volunteerSkills;
    }

    public List<String> getVolunteerSkillTypes() {
        return volunteerSkillTypes;
    }

    public void setVolunteerSkillTypes(List<String> volunteerSkillTypes) {
        this.volunteerSkillTypes = volunteerSkillTypes;
    }
}
