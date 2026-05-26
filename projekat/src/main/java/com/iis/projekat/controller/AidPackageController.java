package com.iis.projekat.controller;

import com.iis.projekat.dto.AidPackageDTO;
import com.iis.projekat.model.AidPackage;
import com.iis.projekat.service.AidPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packages")
public class AidPackageController {

    @Autowired
    private AidPackageService aidPackageService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AidPackageDTO dto) {

        try {

            AidPackage created = aidPackageService.create(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}