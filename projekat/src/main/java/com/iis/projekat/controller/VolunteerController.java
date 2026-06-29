package com.iis.projekat.controller;

import com.iis.projekat.dto.AvailabilityDTO;
import com.iis.projekat.dto.VolunteerDTO;
import com.iis.projekat.dto.VolunteerUpdateDTO;
import com.iis.projekat.model.Availability;
import com.iis.projekat.service.EmailService;
import com.iis.projekat.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<VolunteerDTO>> getAll() {
        return ResponseEntity.ok(volunteerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDTO> getVolunteerById(@PathVariable Long id) {
        VolunteerDTO dto = volunteerService.getVolunteerById(id);
        if(dto == null){
            return new ResponseEntity<VolunteerDTO>((VolunteerDTO) null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<VolunteerDTO>(dto, HttpStatus.OK);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {
        volunteerService.saveImage(id, file);
        return ResponseEntity.ok("Image saved");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVolunteerById(@PathVariable Long id) {
       volunteerService.deleteVolunteer(id);
       return ResponseEntity.ok("Volunteer successfully deleted");
    }

    @GetMapping("/rank/{taskId}")
    public ResponseEntity<List<VolunteerDTO>> rankVolunteers(
            @PathVariable Long taskId) {
        List<VolunteerDTO> ret = volunteerService.rank(taskId);
        if(ret == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(ret);
    }

    //Odavde mi krece Availability
    @PostMapping("/saveAvailability")
    public ResponseEntity<?> saveAvailability(@RequestBody List<AvailabilityDTO> availabilities) {
        return ResponseEntity.ok(
                volunteerService.saveAvailability(availabilities)
        );
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> generateReport(@PathVariable Long id) {
        byte[] pdf = volunteerService.generateReport(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment",
                "report-volunteer-" + id +".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
