package com.iis.projekat.controller;

import com.iis.projekat.dto.BeneficiaryDTO;
import com.iis.projekat.model.Beneficiary;
import com.iis.projekat.service.BeneficiaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiary")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping("/register")
    public ResponseEntity<Beneficiary> register(@RequestBody BeneficiaryDTO dto) {
        try {
            return ResponseEntity.ok(beneficiaryService.saveBeneficiary(dto));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public boolean update(
            @PathVariable Long id,
            @RequestBody BeneficiaryDTO dto
    ) {
        return beneficiaryService.updateBeneficiary(id, dto);
    }
}