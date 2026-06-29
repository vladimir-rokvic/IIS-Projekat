package com.iis.projekat.dto;

import com.iis.projekat.model.FundingRequestStatus;
import com.iis.projekat.model.ProjectFundingRequest;

import java.time.LocalDateTime;

public class FundingRequestResponseDTO {
    public Long id;
    public Long projectId;
    public String projectNaziv;
    public Long koordinatorId;
    public String koordinatorIme;
    public String koordinatorPrezime;
    public Double zahtevanIznos;
    public String razlogZahteva;
    public FundingRequestStatus status;
    public Double odobrenIznos;
    public String razlogOdluke;
    public LocalDateTime datumZahteva;
    public LocalDateTime datumOdluke;

    /**
     * Vidljivo samo menadžeru — koliko opštih sredstava ima organizacija
     * i koliko bi ostalo ako se zahtev odobri.
     */
    public Double trenutnoOpstiIznos;
    public Double preostaloNakonOdobrenja;

    public static FundingRequestResponseDTO from(ProjectFundingRequest r) {
        FundingRequestResponseDTO dto = new FundingRequestResponseDTO();
        dto.id = r.getId();
        dto.projectId = r.getProject().getId();
        dto.projectNaziv = r.getProject().getNaziv();
        dto.koordinatorId = r.getKoordinator().getId();
        dto.koordinatorIme = r.getKoordinator().getName();
        dto.koordinatorPrezime = r.getKoordinator().getSurname();
        dto.zahtevanIznos = r.getZahtevanIznos();
        dto.razlogZahteva = r.getRazlogZahteva();
        dto.status = r.getStatus();
        dto.odobrenIznos = r.getOdobrenIznos();
        dto.razlogOdluke = r.getRazlogOdluke();
        dto.datumZahteva = r.getDatumZahteva();
        dto.datumOdluke = r.getDatumOdluke();
        return dto;
    }
}
