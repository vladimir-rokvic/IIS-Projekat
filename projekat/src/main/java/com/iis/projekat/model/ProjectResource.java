package com.iis.projekat.model;

import jakarta.persistence.*;

/**
 * Namenska sredstva dodeljena konkretnom projektu.
 * Koordinator može da vidi ova sredstva, ali ne i opšta sredstva organizacije.
 */
@Entity
@Table(name = "project_resource")
public class ProjectResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /** Naziv/opis resursa (npr. "Budžet za opremu", "Vozila", "Finansijska sredstva") */
    @Column(nullable = false)
    private String naziv;

    /** Ukupan iznos/količina namenskih sredstava */
    @Column(nullable = false)
    private Double ukupnoSredstava;

    /** Trenutno dostupan iznos (ukupno minus potrošeno) */
    @Column(nullable = false)
    private Double dostupnoSredstava;

    /** Opcioni opis/napomena o resursu */
    @Column(columnDefinition = "TEXT")
    private String opis;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public Double getUkupnoSredstava() { return ukupnoSredstava; }
    public void setUkupnoSredstava(Double ukupnoSredstava) { this.ukupnoSredstava = ukupnoSredstava; }

    public Double getDostupnoSredstava() { return dostupnoSredstava; }
    public void setDostupnoSredstava(Double dostupnoSredstava) { this.dostupnoSredstava = dostupnoSredstava; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
}
