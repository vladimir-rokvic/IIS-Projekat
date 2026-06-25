package com.iis.projekat.model;

import jakarta.persistence.*;

/**
 * Opšta sredstva organizacije (iz donacija koje nisu namenjene konkretnom projektu).
 * Koordinator ne može da vidi ovaj iznos — vidljiv je samo menadžeru.
 * Postoji tačno jedan zapis (singleton pattern po organizaciji).
 */
@Entity
@Table(name = "organization_funds")
public class OrganizationFunds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double dostupnoSredstava = 0.0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getDostupnoSredstava() { return dostupnoSredstava; }
    public void setDostupnoSredstava(Double dostupnoSredstava) { this.dostupnoSredstava = dostupnoSredstava; }
}
