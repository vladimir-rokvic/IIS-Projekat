package com.iis.projekat.service;

import com.iis.projekat.dto.VolunteerDTO;
import com.iis.projekat.dto.VolunteerUpdateDTO;
import com.iis.projekat.model.Address;
import com.iis.projekat.model.Skill;
import com.iis.projekat.model.Volunteer;
import com.iis.projekat.repository.AddressRepository;
import com.iis.projekat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VolunteerService {
    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;

    public VolunteerService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean saveVolunteer(VolunteerUpdateDTO dto) {
        if(volunteerRepository.existsByEmail(dto.getEmail())) {
            return false;
        }

        Volunteer v = new Volunteer();
        Address a = null;

        if(addressRepository.existsByCityAndStreetAndCountry(
                dto.getCity(),
                dto.getStreet(),
                dto.getCountry()
        )) {
            a = addressRepository.findByCityAndStreetAndCountry(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );
        } else {
            a = new Address(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );

            addressRepository.save(a);
        }

        v.setAddress(a);
        v.setName(dto.getName());
        v.setSurname(dto.getSurname());
        v.setPassword(passwordEncoder.encode(dto.getPassword()));
        v.setDateOfBirth(dto.getDob());
        v.setEmail(dto.getEmail());
        v.setPhone(dto.getPhone());

        volunteerRepository.save(v);
        return true;
    }

    public boolean updateVolunteer(Long id, VolunteerUpdateDTO dto) {
        Optional<Volunteer> v = volunteerRepository.findById(id);
        if(v.isEmpty()) return false;

        Volunteer oldVolunteer = v.get();
        Address a = null;

        if(addressRepository.existsByCityAndStreetAndCountry(
                dto.getCity(),
                dto.getStreet(),
                dto.getCountry()
        )) {
            a = addressRepository.findByCityAndStreetAndCountry(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );
        } else {
            a = new Address(
                    dto.getCity(),
                    dto.getStreet(),
                    dto.getCountry()
            );

            addressRepository.save(a);
        }
        oldVolunteer.setPhone(dto.getPhone());
        oldVolunteer.setEmail(dto.getEmail());
        oldVolunteer.setDateOfBirth(dto.getDob());
        oldVolunteer.setName(dto.getName());
        oldVolunteer.setSurname(dto.getSurname());
        oldVolunteer.setAddress(a);
        oldVolunteer.setBio(dto.getBio());
        if(dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            oldVolunteer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getSkills() != null) {
            Set<Skill> skills = dto.getSkills().stream().map(skillDTO -> {
                Skill s = new Skill();
                s.setName(skillDTO.getName());
                s.setDescription(skillDTO.getDesc());
                return s;
            }).collect(Collectors.toSet());
            oldVolunteer.setSkills(skills);
        }

        volunteerRepository.save(oldVolunteer);
        return true;
    }

    public List<VolunteerDTO> getAll() {
        List<VolunteerDTO> ret = new ArrayList<>();
        for(Volunteer v: volunteerRepository.findAll()) {
            ret.add(new VolunteerDTO(v));
        }
        return ret;
    }

    public VolunteerDTO getVolunteerById(Long id) {
        Volunteer v = volunteerRepository.findById(id).orElseThrow();
        return new VolunteerDTO(v);
    }

    public void deleteVolunteer(Long id) {
        volunteerRepository.delete(volunteerRepository.getReferenceById(id));
    }
}
