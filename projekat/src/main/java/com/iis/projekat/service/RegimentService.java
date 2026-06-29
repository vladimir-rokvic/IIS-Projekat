package com.iis.projekat.service;

import com.iis.projekat.dto.RegimentDTO;
import com.iis.projekat.model.Certificate;
import com.iis.projekat.model.Regiment;
import com.iis.projekat.model.Volunteer;
import com.iis.projekat.repository.CertificateRepository;
import com.iis.projekat.repository.RegimentRepository;
import com.iis.projekat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RegimentService {
    @Autowired
    private RegimentRepository regimentRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private CertificateRepository certificateRepository;

    //U specifikaciji sam napisao da trebju da se random generisu grupe za volontere
    private List<Volunteer> createTraineeGroup(List<Volunteer> volunteers, int numberToGroup) {
        Random r = new Random();

        List<Volunteer> trainees = new ArrayList<>();
        for(int i=0;i<numberToGroup;++i) {
            int index = r.nextInt(volunteers.size());
            trainees.add(volunteers.get(index));
            volunteers.remove(index);
        }

        return trainees;
    }

    public Regiment save(RegimentDTO regiment) {
        Regiment r = new Regiment();

        if(regiment.getDescription() == null) return null;

        r.setDescription(regiment.getDescription());
        r.setStartDate(regiment.getStartDate());
        r.setEndDate(regiment.getEndDate());

        r.setCertificate(regiment.getCertificate());

        Volunteer trainer = volunteerRepository.findById(regiment.getTrainer().getId()).orElse(null);
        if(trainer == null) return null;
        r.setTrainer(trainer);

        List<Volunteer> volunteers = volunteerRepository.findAllNotInTraining();
        if(volunteers.isEmpty()) return null;
        volunteers.removeIf(v -> v.getId().equals(trainer.getId()));
        r.setNumOfTrainees(Math.min(volunteers.size(), regiment.getNumOfTrainees()));
        List<Volunteer> trainees = createTraineeGroup(volunteers, regiment.getNumOfTrainees());

        r.setTrainees(trainees);

        return regimentRepository.save(r);
    }

    public RegimentDTO update(RegimentDTO regiment) {
        Regiment r = regimentRepository.findById(regiment.getId()).orElse(null);
        if(r == null) return null;

        Volunteer trainer = volunteerRepository.findById(regiment.getTrainer().getId()).orElse(null);
        if(trainer == null) return null;
        r.setTrainer(trainer);
        r.setDescription(regiment.getDescription());
        r.setEndDate(regiment.getEndDate());
        r.setStartDate(regiment.getStartDate());

        return regiment;
    }

    public List<RegimentDTO> getAll() {
        List<Regiment> regiments = regimentRepository.findAll();

        List<RegimentDTO> ret = new ArrayList<>();
        for(Regiment r: regiments) {
            ret.add(new RegimentDTO(r));
        }

        return ret;
    }

    public RegimentDTO findById(Long id) {
        Regiment r = regimentRepository.findById(id).orElse(null);
        if(r == null) return null;
        else return new RegimentDTO(r);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public List<RegimentDTO> getForVolunteer(Long id) {
        List<Regiment> trainee_regiments = regimentRepository.findAllByTraineesId(id);
        List<Regiment> trainer_regiments = regimentRepository.findAllByTrainerId(id);

        List<RegimentDTO> ret = new ArrayList<>();

        for(Regiment r: trainee_regiments) {
            ret.add(new RegimentDTO(r));
        }
        for(Regiment r: trainer_regiments) {
            ret.add(new RegimentDTO(r));
        }

        return ret;
    }
}
