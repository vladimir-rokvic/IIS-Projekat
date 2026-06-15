package com.iis.projekat.dto;

import java.util.List;

public class PredictRequestDTO {
    private List<String> taskSkillTypes;
    private List<VolunteerPredictDTO> volunteers;

    public PredictRequestDTO() {}

    public List<String> getTaskSkillTypes() {
        return taskSkillTypes;
    }

    public void setTaskSkillTypes(List<String> taskSkillTypes) {
        this.taskSkillTypes = taskSkillTypes;
    }

    public List<VolunteerPredictDTO> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<VolunteerPredictDTO> volunteers) {
        this.volunteers = volunteers;
    }
}
