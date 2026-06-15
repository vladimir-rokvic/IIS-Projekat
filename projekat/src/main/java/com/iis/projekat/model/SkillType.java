package com.iis.projekat.model;

import jakarta.persistence.*;

/**
 * Katalog veština u sistemu.
 * Ovo je "tip" veštine — ne vezan za konkretnog volontera ili zadatak.
 * Faze projekta referenciraju SkillType, a volonteri imaju Skill koji ima @ManyToOne ka SkillType.
 */
@Entity
@Table(name = "skill_type")
public class SkillType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
