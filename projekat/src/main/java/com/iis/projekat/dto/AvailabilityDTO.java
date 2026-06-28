package com.iis.projekat.dto;

import com.iis.projekat.model.Availability;
import com.iis.projekat.model.WeekDays;

public class AvailabilityDTO {
    private Long id;
    private int startHour;
    private int endHour;

    private Long volunteerId;
    private Long taskId;

    private WeekDays day;

    private boolean enabled;

    public AvailabilityDTO() {}

    public AvailabilityDTO(Availability a) {
        id = a.getId();
        startHour = a.getStartHour();
        endHour = a.getEndHour();
        day = a.getDay();
        enabled = a.isEnabled();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public WeekDays getDay() {
        return day;
    }

    public void setDay(WeekDays day) {
        this.day = day;
    }
}
