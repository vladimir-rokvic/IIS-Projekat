package com.iis.projekat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Zahtev koordinatora za dobijanje dodatnih sredstava iz opštih donacija organizacije.
 * Koordinator navodi iznos i razlog; menadžer odlučuje i navodi razlog svoje odluke.
 */
@Entity
@Table(name = "project_funding_request")
public class ProjectFundingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "koordinator_id", nullable = false)
    private Employee koordinator;

    /** Iznos koji koordinator zahteva */
    @Column(nullable = false)
    private Double zahtevanIznos;

    /** Razlog zašto su sredstva neophodna */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String razlogZahteva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundingRequestStatus status = FundingRequestStatus.NA_CEKANJU;

    /** Iznos koji je menadžer odobrio (može biti manji od zahtevanog) */
    private Double odobrenIznos;

    /** Razlog odluke menadžera (obavezan za delimično odobrenje i odbijanje) */
    @Column(columnDefinition = "TEXT")
    private String razlogOdluke;

    @Column(nullable = false)
    private LocalDateTime datumZahteva = LocalDateTime.now();

    private LocalDateTime datumOdluke;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Employee getKoordinator() { return koordinator; }
    public void setKoordinator(Employee koordinator) { this.koordinator = koordinator; }

    public Double getZahtevanIznos() { return zahtevanIznos; }
    public void setZahtevanIznos(Double zahtevanIznos) { this.zahtevanIznos = zahtevanIznos; }

    public String getRazlogZahteva() { return razlogZahteva; }
    public void setRazlogZahteva(String razlogZahteva) { this.razlogZahteva = razlogZahteva; }

    public FundingRequestStatus getStatus() { return status; }
    public void setStatus(FundingRequestStatus status) { this.status = status; }

    public Double getOdobrenIznos() { return odobrenIznos; }
    public void setOdobrenIznos(Double odobrenIznos) { this.odobrenIznos = odobrenIznos; }

    public String getRazlogOdluke() { return razlogOdluke; }
    public void setRazlogOdluke(String razlogOdluke) { this.razlogOdluke = razlogOdluke; }

    public LocalDateTime getDatumZahteva() { return datumZahteva; }
    public void setDatumZahteva(LocalDateTime datumZahteva) { this.datumZahteva = datumZahteva; }

    public LocalDateTime getDatumOdluke() { return datumOdluke; }
    public void setDatumOdluke(LocalDateTime datumOdluke) { this.datumOdluke = datumOdluke; }
}
