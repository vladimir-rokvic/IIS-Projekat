package com.iis.projekat.controller;

import com.iis.projekat.dto.DonorCreateDTO;
import com.iis.projekat.dto.DonorDTO;
import com.iis.projekat.dto.DonorProfileDTO;
import com.iis.projekat.service.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    @Autowired
    private DonorService donorService;

    @PostMapping
    public ResponseEntity<DonorDTO> createDonor(@RequestBody DonorCreateDTO dto) {
        DonorDTO res = donorService.createDonor(dto);
        if(res == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonor(@PathVariable Long id, @RequestBody DonorCreateDTO dto) {
        boolean ok = donorService.updateDonor(id, dto);
        if(!ok) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonorProfileDTO> getDonor(@PathVariable Long id) {
        DonorProfileDTO dto = donorService.getDonorProfileById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<DonorDTO>> listDonors() {
        return ResponseEntity.ok(donorService.listDonors());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return ResponseEntity.ok().build();
    }
}
