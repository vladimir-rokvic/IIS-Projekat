package com.iis.projekat.dto;

import com.iis.projekat.model.Kpi;

public class KpiResponseDTO {
    public Long id;
    public Long projectId;
    public String opis;
    public String intervalMerenja;

    public static KpiResponseDTO from(Kpi k) {
        KpiResponseDTO dto = new KpiResponseDTO();
        dto.id = k.getId();
        dto.projectId = k.getProject().getId();
        dto.opis = k.getOpis();
        dto.intervalMerenja = k.getIntervalMerenja().name();
        return dto;
    }
}
