package com.iis.projekat.controller.Beneficiary;

import com.iis.projekat.dto.Beneficiary.AidHistoryResponse;
import com.iis.projekat.dto.Beneficiary.AidPackageDTO;
import com.iis.projekat.model.Beneficiary.AidPackage;
import com.iis.projekat.service.Beneficiary.AidPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/history/{id}")
    public ResponseEntity<List<AidHistoryResponse>> getHistory(@PathVariable Long id){
        return ResponseEntity.ok(aidPackageService.getHistory(id));
    }
}