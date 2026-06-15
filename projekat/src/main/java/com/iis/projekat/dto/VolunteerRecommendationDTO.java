package com.iis.projekat.dto;

import com.iis.projekat.model.Volunteer;

import java.util.List;

/**
 * Predstavlja jednog volontera preporučenog za fazu projekta,
 * zajedno sa informacijom o tome koliko od traženih veština poseduje
 * i da li je slobodan u periodu trajanja faze.
 */
public class VolunteerRecommendationDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;

    /** Broj veština iz potrebneVestine koje volonter poseduje. */
    private int matchedSkillsCount;

    /** Ukupan broj traženih veština za fazu. */
    private int totalRequiredSkills;

    /** Nazivi veština koje volonter poseduje i koje su tražene za fazu. */
    private List<String> matchedSkillNames;

    /** Da li je volonter slobodan (nema preklapajući task) u periodu trajanja faze. */
    private boolean available;

    /**
     * Da li je volonter "zakucan" na ovoj fazi — tj. već je dodeljen
     * na bar jedan task unutar ove faze. Zakucani volonteri se uvek
     * prikazuju u preporuci, bez obzira na trenutne skillove/dostupnost.
     */
    private boolean pinned;

    public VolunteerRecommendationDTO() {}

    public VolunteerRecommendationDTO(Volunteer v, int matchedSkillsCount, int totalRequiredSkills,
                                       List<String> matchedSkillNames, boolean available, boolean pinned) {
        this.id = v.getId();
        this.name = v.getName();
        this.surname = v.getSurname();
        this.email = v.getEmail();
        this.matchedSkillsCount = matchedSkillsCount;
        this.totalRequiredSkills = totalRequiredSkills;
        this.matchedSkillNames = matchedSkillNames;
        this.available = available;
        this.pinned = pinned;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getMatchedSkillsCount() { return matchedSkillsCount; }
    public void setMatchedSkillsCount(int matchedSkillsCount) { this.matchedSkillsCount = matchedSkillsCount; }

    public int getTotalRequiredSkills() { return totalRequiredSkills; }
    public void setTotalRequiredSkills(int totalRequiredSkills) { this.totalRequiredSkills = totalRequiredSkills; }

    public List<String> getMatchedSkillNames() { return matchedSkillNames; }
    public void setMatchedSkillNames(List<String> matchedSkillNames) { this.matchedSkillNames = matchedSkillNames; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
