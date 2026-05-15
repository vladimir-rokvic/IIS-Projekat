package com.iis.projekat.service;

import com.iis.projekat.dto.VolunteerDTO;
import com.iis.projekat.dto.VolunteerUpdateDTO;
import com.iis.projekat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VolunteerService {
    @Autowired
    private VolunteerRepository volunteerRepository;

    //TODO
    public void saveVolunteer(VolunteerUpdateDTO dto) {
        return;
    }

    //TODO
    public void updateVolunteer(VolunteerUpdateDTO dto) {
        return;
    }

    //TODO
    public VolunteerDTO getVolunteerById(Long id) {
        return null;
    }

    //TODO
    public void deleteVolunteer(Long id) {
        return;
    }


}
