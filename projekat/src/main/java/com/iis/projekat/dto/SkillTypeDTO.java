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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
