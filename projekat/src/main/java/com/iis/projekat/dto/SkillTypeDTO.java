package com.iis.projekat.dto;

import com.iis.projekat.model.SkillType;

public class SkillTypeDTO {
    public Long id;
    public String name;
    public String description;

    public static SkillTypeDTO from(SkillType st) {
        SkillTypeDTO dto = new SkillTypeDTO();
        dto.id = st.getId();
        dto.name = st.getName();
        dto.description = st.getDescription();
        return dto;
    }
}
