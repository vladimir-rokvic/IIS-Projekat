package com.iis.projekat.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO koji nosi sve podatke potrebne za generisanje PDF izveštaja o projektu.
 * Puni se u ProjectService i prosleđuje ProjectReportService-u.
 */
public class ProjectReportDTO {
    public Long   projektId;
    public String naziv;
    public String opis;
    public String ciljevi;
    public LocalDate rokPocetak;
    public LocalDate rokKraj;
    public String status;
    public String geografskaLokacija;
    public String ciljnaGrupa;
    public String izvoriFinansiranja;

    public String koordinatorIme;       // ime i prezime glavnog koordinatora
    public List<String> pomocniKoordinatori; // lista "Ime Prezime" pomoćnih

    public String kpiOpis;
    public String kpiIntervalMerenja;

    public Double ukupnoDonirano;       // suma svih donacija za projekat
    public Integer brojDonatora;        // broj jedinstvenih donatora

    public int ukupnoFaza;
    public int zavrsenihFaza;
    public List<FazaIzvestajDTO> faze;

    public int ukupnoVolontera;             // broj angažovanih volontera
    public Double prosecnaOcenaVolontera;   // prosek svih ocena (Performance)
    public int ukupnoZadataka;
    public int zavrseniZadaci;              // taskovi kojima je endDate prošao


    /** Podaci o jednoj fazi projekta (ugnježdeni u izveštaj). */
    public static class FazaIzvestajDTO {
        public String  naziv;
        public LocalDate rokPocetak;
        public LocalDate rokKraj;
        public boolean zavrsena;
        public int brojZadataka;
        public int brojVolontera;  // planiran broj volontera (phase.brojVolontera)
        public List<String> potrebneVestine; // nazivi SkillType-ova
    }
}
