package com.iis.projekat.controller;

import com.iis.projekat.dto.VolunteerDTO;
import com.iis.projekat.dto.VolunteerUpdateDTO;
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

    //TODO: iskreno ne znam sta da radim ovde kada vec imamo register request
    @PostMapping
    public ResponseEntity<?> createVolunteer() {
        return null;
    }

    @PutMapping
    public ResponseEntity<?> updateVolunteer(@RequestBody VolunteerUpdateDTO dto) {
        //TODO: error handling
        volunteerService.updateVolunteer(dto);
        return ResponseEntity.ok("TODO");
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDTO> getVolunteerById(@PathVariable Long id) {
        return new ResponseEntity<VolunteerDTO>(volunteerService.getVolunteerById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVolunteerById(@PathVariable Long id) {
       volunteerService.deleteVolunteer(id);
       return ResponseEntity.ok("Volunteer successfully deleted");
    }
}
