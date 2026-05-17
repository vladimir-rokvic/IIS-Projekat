package com.iis.projekat.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Koristi se za editovanje projekta dok je U_PRIPREMI statusu.
 * Dokument se šalje odvojeno kao multipart/form-data.
 */
public class UpdateProjectRequest {

    public String naziv;
    public String opis;
    public String ciljevi;
    public LocalDate rokPocetak;
    public LocalDate rokKraj;
    public String status;

    // Opciona polja
    public String ciljnaGrupa;
    public String geografskaLokacija;
    public String izvoriFinansiranja;

    // ID-evi pomoćnih koordinatora (može biti prazna lista)
    public List<Long> pomocniKoordinatoriIds;
}
