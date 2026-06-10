package com.iis.projekat.model;

import jakarta.persistence.*;

@Entity
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double grade;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Volunteer ratedVolunteer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    public Performance(Double grade, String comment, Volunteer v, Task t) {
        this.grade = grade;
        this.comment = comment;
        this.ratedVolunteer = v;
        this.task = t;
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

    public Volunteer getRatedVolunteer() {
        return ratedVolunteer;
    }

    public void setRatedVolunteer(Volunteer ratedVolunteer) {
        this.ratedVolunteer = ratedVolunteer;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
