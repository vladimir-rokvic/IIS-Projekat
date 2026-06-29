package com.iis.projekat.controller;

import com.iis.projekat.dto.RegimentDTO;
import com.iis.projekat.model.Certificate;
import com.iis.projekat.model.Regiment;
import com.iis.projekat.service.RegimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regiment")
public class RegimentController {
    @Autowired
    private RegimentService regimentService;

    @GetMapping("/")
    public ResponseEntity<List<RegimentDTO>> getAll() {
        return ResponseEntity.ok(regimentService.getAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> saveRegiment(@RequestBody RegimentDTO regimentDTO) {
        Regiment regiment = regimentService.save(regimentDTO);
        if(regiment == null) {
            return  ResponseEntity.badRequest().build();
        }

        return  ResponseEntity.ok(regiment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegimentDTO> getRegimentById(@PathVariable Long id) {
        RegimentDTO ret = regimentService.findById(id);
        if(ret == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(ret);
    }

    @GetMapping("/allCertificates")
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        return ResponseEntity.ok(regimentService.getAllCertificates());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegimentDTO> UpdateRegiment(@RequestBody RegimentDTO regimentDTO) {
        RegimentDTO ret = regimentService.update(regimentDTO);
        if(ret == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(ret);
    }

    //dobavi za volontera, da i za trenere i
    //za one koji polazu
    @GetMapping("/volunteer/{id}")
    public ResponseEntity<List<RegimentDTO>> getForVolunteer(@PathVariable Long id) {
        return ResponseEntity.ok(regimentService.getForVolunteer(id));
    }
}
