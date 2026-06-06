package com.iis.projekat.dto;

import java.util.List;

/**
 * Podaci za kreiranje jedne faze projekta.
 * Šalje se kao element liste u POST /api/projekti/{id}/faze.
 */
public class ProjectPhaseCreateDTO {
    public String naziv;
    public String ciljevi;
    public String rokPocetak;   // format: "yyyy-MM-dd"
    public String rokKraj;      // format: "yyyy-MM-dd"
    public Integer brojVolontera;
    public List<Long> potrebneVestineIds;  // ID-evi SkillType entiteta
    public Integer redosled;
}
