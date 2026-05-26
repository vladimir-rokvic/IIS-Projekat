package com.iis.projekat.controller;

import com.iis.projekat.dto.DistributionLocationDTO;
import com.iis.projekat.model.DistributionLocation;
import com.iis.projekat.service.DistributionLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/distribution")
public class DistributionController {

    @Autowired
    private DistributionLocationService distributionLocationService;

    @PostMapping("/location/create")
    public ResponseEntity<?> create(@RequestBody DistributionLocationDTO dto) {

        try {

            DistributionLocation created =
                    distributionLocationService.create(dto);

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