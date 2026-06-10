package com.iis.projekat.dto;

import com.iis.projekat.model.Performance;

public class PerformanceDTO {
    private Long id;
    private Double grade;
    private String comment;
    private Long volunteerId;
    private Long taskId;
    private Long coordinatorId;

    public PerformanceDTO() {}

    public PerformanceDTO(Performance performance) {
        this.id = performance.getId();
        this.grade = performance.getGrade();
        this.comment = performance.getComment();
        this.volunteerId = performance.getRatedVolunteer().getId();
        this.taskId =  performance.getTask().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }
}
