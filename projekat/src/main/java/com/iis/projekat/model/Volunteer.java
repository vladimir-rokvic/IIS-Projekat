package com.iis.projekat.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "volunteers")
public class Volunteer extends User {
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Skill> skills;

    @OneToMany(mappedBy = "volunteer")
    private Set<Task> tasks;

    @ManyToMany
    @JoinTable(
            name = "volunteer_skill_types",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_type_id")
    )
    private List<SkillType> volunteerSkillTypes = new ArrayList<>();

    private String bio;

    public Volunteer() {}

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public List<SkillType> getVolunteerSkillTypes() {
        return volunteerSkillTypes;
    }

    public void setVolunteerSkillTypes(List<SkillType> volunteerSkillTypes) {
        this.volunteerSkillTypes = volunteerSkillTypes;
    }
}
