package com.iis.projekat.controller.Beneficiary;

import com.iis.projekat.dto.Beneficiary.BeneficiaryDTO;
import com.iis.projekat.dto.NeedsReassessmentRequestDTO;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import com.iis.projekat.service.BeneficiaryService;

import com.iis.projekat.service.NeedsReassessmentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiary")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private NeedsReassessmentRequestService needsReassessmentRequestService;

    @PostMapping("/register")
    public ResponseEntity<Beneficiary> register(@RequestBody BeneficiaryDTO dto) {
        try {
            return ResponseEntity.ok(beneficiaryService.saveBeneficiary(dto));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reassessment-requests/create")
    public ResponseEntity<String> create(@RequestBody NeedsReassessmentRequestDTO dto) {
        try {
            needsReassessmentRequestService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Created.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
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