package com.iis.projekat.dto;

/**
 * Menadžer popunjava ovo kada donosi odluku o zahtevu za sredstva.
 *
 * Moguće vrednosti za status:
 *   ODOBREN          - odobrava ceo traženi iznos (razlog nije obavezan)
 *   DELIMICNO_ODOBREN - odobrava deo iznosa; odobrenIznos i razlogOdluke su obavezni
 *   ODBIJEN          - odbija zahtev; razlogOdluke je obavezan
 */
public class ManagerFundingDecisionDTO {
    public String status;
    public Double odobrenIznos;
    public String razlogOdluke;
}
