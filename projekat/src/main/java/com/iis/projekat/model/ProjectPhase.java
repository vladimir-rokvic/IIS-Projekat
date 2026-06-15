package com.iis.projekat.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_phase")
public class ProjectPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String naziv;

    @Column(columnDefinition = "TEXT")
    private String ciljevi;

    @Column(nullable = false)
    private LocalDate rokPocetak;

    @Column(nullable = false)
    private LocalDate rokKraj;

    @Column(nullable = false)
    private Integer brojVolontera;

    /**
     * Veštine neophodne za ovu fazu — referenciraju katalog SkillType.
     * Koristiće se za matching volontera sa fazama.
     */
    @ManyToMany
    @JoinTable(
            name = "phase_skill_types",
            joinColumns = @JoinColumn(name = "phase_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_type_id")
    )
    private List<SkillType> potrebneVestine = new ArrayList<>();

    /** Redosled faze unutar projekta (1, 2, 3...). */
    @Column(nullable = false)
    private Integer redosled;

    /** Pomoćni koordinatori koji nadgledaju ovu konkretnu fazu. */
    @ManyToMany
    @JoinTable(
            name = "phase_pomocni_koordinatori",
            joinColumns = @JoinColumn(name = "phase_id"),
            inverseJoinColumns = @JoinColumn(name = "koordinator_id")
    )
    private List<Employee> pomocniKoordinatori = new ArrayList<>();

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskovi = new ArrayList<>();

    @Column(nullable = false)
    private boolean zavrsena = false;

    public Long getId() { return id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public String getCiljevi() { return ciljevi; }
    public void setCiljevi(String ciljevi) { this.ciljevi = ciljevi; }

    public LocalDate getRokPocetak() { return rokPocetak; }
    public void setRokPocetak(LocalDate rokPocetak) { this.rokPocetak = rokPocetak; }

    public LocalDate getRokKraj() { return rokKraj; }
    public void setRokKraj(LocalDate rokKraj) { this.rokKraj = rokKraj; }

    public Integer getBrojVolontera() { return brojVolontera; }
    public void setBrojVolontera(Integer brojVolontera) { this.brojVolontera = brojVolontera; }

    public List<SkillType> getPotrebneVestine() { return potrebneVestine; }
    public void setPotrebneVestine(List<SkillType> potrebneVestine) { this.potrebneVestine = potrebneVestine; }

    public Integer getRedosled() { return redosled; }
    public void setRedosled(Integer redosled) { this.redosled = redosled; }

    public List<Employee> getPomocniKoordinatori() { return pomocniKoordinatori; }
    public void setPomocniKoordinatori(List<Employee> pomocniKoordinatori) {
        this.pomocniKoordinatori = pomocniKoordinatori;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Task> getTaskovi() {
        return taskovi;
    }

    public void setTaskovi(List<Task> taskovi) {
        this.taskovi = taskovi;
    }

    public boolean isZavrsena() {
        return zavrsena;
    }

    public void setZavrsena(boolean zavrsena) {
        this.zavrsena = zavrsena;
    }
}
