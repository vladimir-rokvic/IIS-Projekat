package com.iis.projekat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kpi")
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String opis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationFrequency intervalMerenja;

    @OneToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    public Long getId() { return id; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public NotificationFrequency getIntervalMerenja() { return intervalMerenja; }
    public void setIntervalMerenja(NotificationFrequency intervalMerenja) { this.intervalMerenja = intervalMerenja; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}
