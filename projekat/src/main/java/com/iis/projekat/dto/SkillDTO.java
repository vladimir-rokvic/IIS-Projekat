package com.iis.projekat.dto;

//da li mi je ovo uopste i potrebno ili samo da sve radim preko skill klase
public class SkillDTO {
    private String name;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
