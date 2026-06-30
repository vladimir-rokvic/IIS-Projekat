package com.iis.projekat.dto;

/**
 * Koordinator popunjava ovo kada traži dodatna sredstva za projekat.
 */
public class FundingRequestCreateDTO {
    public Long projectId;
    public Double zahtevanIznos;
    public String razlogZahteva;
}
