package com.iis.projekat.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String opis;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ciljevi;

    @Column(nullable = false)
    private LocalDate rokPocetak;

    @Column(nullable = false)
    private LocalDate rokKraj;

    @ManyToOne(optional = false)
    @JoinColumn(name = "koordinator_id", nullable = false)
    private Employee koordinator;

    private String ciljnaGrupa;

    private String geografskaLokacija;

    @Column(columnDefinition = "TEXT")
    private String izvoriFinansiranja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.U_PRIPREMI;

    // Dokument - čuvamo ime fajla i sadržaj kao bytes u bazi.
    private String dokumentIme;

    @Column(name = "dokument_sadrzaj", columnDefinition = "BYTEA")
    private byte[] dokumentSadrzaj;

    @ManyToMany
    @JoinTable(
            name = "project_pomocni_koordinatori",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "koordinator_id")
    )
    private List<Employee> pomocniKoordinatori = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String razlog;

    public String getRazlog() {
        return razlog;
    }

    public void setRazlog(String razlog) {
        this.razlog = razlog;
    }

    public Long getId() { return id; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public String getCiljevi() { return ciljevi; }
    public void setCiljevi(String ciljevi) { this.ciljevi = ciljevi; }

    public LocalDate getRokPocetak() { return rokPocetak; }
    public void setRokPocetak(LocalDate rokPocetak) { this.rokPocetak = rokPocetak; }

    public LocalDate getRokKraj() { return rokKraj; }
    public void setRokKraj(LocalDate rokKraj) { this.rokKraj = rokKraj; }

    public Employee getKoordinator() { return koordinator; }
    public void setKoordinator(Employee koordinator) { this.koordinator = koordinator; }

    public String getCiljnaGrupa() { return ciljnaGrupa; }
    public void setCiljnaGrupa(String ciljnaGrupa) { this.ciljnaGrupa = ciljnaGrupa; }

    public String getGeografskaLokacija() { return geografskaLokacija; }
    public void setGeografskaLokacija(String geografskaLokacija) { this.geografskaLokacija = geografskaLokacija; }

    public String getIzvoriFinansiranja() { return izvoriFinansiranja; }
    public void setIzvoriFinansiranja(String izvoriFinansiranja) { this.izvoriFinansiranja = izvoriFinansiranja; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public String getDokumentIme() { return dokumentIme; }
    public void setDokumentIme(String dokumentIme) { this.dokumentIme = dokumentIme; }

    public byte[] getDokumentSadrzaj() { return dokumentSadrzaj; }
    public void setDokumentSadrzaj(byte[] dokumentSadrzaj) { this.dokumentSadrzaj = dokumentSadrzaj; }

    public List<Employee> getPomocniKoordinatori() { return pomocniKoordinatori; }
    public void setPomocniKoordinatori(List<Employee> pomocniKoordinatori) {
        this.pomocniKoordinatori = pomocniKoordinatori;
    }
}
