package com.iis.projekat.dto;

import java.util.List;

/**
 * Request body za POST /api/projekti/{id}/faze.
 * Koordinator šalje sve faze odjednom zajedno sa opcijom preklapanja.
 */
public class SetFazeRequest {
    public boolean fazeMoguDaSePreklapaju;
    public List<ProjectPhaseCreateDTO> faze;
}
