package com.iis.projekat.controller;

import com.iis.projekat.dto.VolunteerDTO;
import com.iis.projekat.dto.VolunteerUpdateDTO;
import com.iis.projekat.service.EmailService;
import com.iis.projekat.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {
    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> createVolunteer(@RequestBody VolunteerUpdateDTO dto) {
        boolean res = volunteerService.saveVolunteer(dto);
        if(res) {
            emailService.sendVolunteerWelcomeMail(
                    dto.getEmail(),
                    dto.getName(),
                    dto.getSurname()
            );
            return ResponseEntity.ok("Alls good");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVolunteer(@PathVariable Long id, @RequestBody VolunteerUpdateDTO dto) {
        boolean r = volunteerService.updateVolunteer(id, dto);
        if(!r) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Alls good");
    }

    @GetMapping("/")
    public ResponseEntity<VolunteerDTO> getVolunteerProfile() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDTO> getVolunteerById(@PathVariable Long id) {
        VolunteerDTO dto = volunteerService.getVolunteerById(id);
        if(dto == null){
            return new ResponseEntity<VolunteerDTO>((VolunteerDTO) null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<VolunteerDTO>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVolunteerById(@PathVariable Long id) {
       volunteerService.deleteVolunteer(id);
       return ResponseEntity.ok("Volunteer successfully deleted");
    }
}
