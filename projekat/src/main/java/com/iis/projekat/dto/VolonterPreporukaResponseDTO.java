package com.iis.projekat.dto;

import java.util.List;

/**
 * Odgovor na zahtev za preporukom volontera za fazu projekta.
 */
public class VolonterPreporukaResponseDTO {
    /** Lista preporučenih volontera, sortirana po broju poklapajućih veština (opadajuće). */
    public List<VolunteerRecommendationDTO> preporuceni;

    /** Broj volontera koji su traženi za fazu (brojVolontera). */
    public int trazenBrojVolontera;

    /**
     * Poruka koja se prikazuje koordinatoru ako je broj pronađenih
     * dostupnih i podudarajućih volontera manji od traženog broja.
     * Null ako je broj dovoljan.
     */
    public String poruka;

    public VolonterPreporukaResponseDTO() {}

    public VolonterPreporukaResponseDTO(List<VolunteerRecommendationDTO> preporuceni,
                                         int trazenBrojVolontera, String poruka) {
        this.preporuceni = preporuceni;
        this.trazenBrojVolontera = trazenBrojVolontera;
        this.poruka = poruka;
    }
}
