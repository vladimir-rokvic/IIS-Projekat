package com.iis.projekat.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "volunteers")
public class Volunteer extends User {
    @OneToMany
    private Set<Skill> skills;

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
}
