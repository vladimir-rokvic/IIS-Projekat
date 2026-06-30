package com.iis.projekat.controller.Beneficiary;

import com.iis.projekat.dto.Beneficiary.BeneficiaryDTO;
import com.iis.projekat.dto.Beneficiary.BeneficiaryDetailsResponse;
import com.iis.projekat.dto.Beneficiary.BeneficiaryPackageResponse;
import com.iis.projekat.dto.NeedsReassessmentRequestDTO;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import com.iis.projekat.service.Beneficiary.BeneficiaryService;

import com.iis.projekat.service.NeedsReassessmentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> get(@PathVariable Long id){

        Beneficiary b = beneficiaryService.getById(id);

        if(b == null){
            return ResponseEntity.notFound().build();
        }

        BeneficiaryDTO dto = new BeneficiaryDTO();

        dto.setEmail(b.getEmail());
        dto.setName(b.getName());
        dto.setSurname(b.getSurname());
        dto.setDateOfBirth(b.getDateOfBirth());
        dto.setPhone(b.getPhone());

        dto.setCity(b.getAddress().getCity());
        dto.setStreet(b.getAddress().getStreet());
        dto.setCountry(b.getAddress().getCountry());

        dto.setEligible(b.isEligible());
        dto.setType(b.getType());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryPackageResponse>> getAll(){
        return ResponseEntity.ok(beneficiaryService.getAll());
    }

    @GetMapping("/details")
    public ResponseEntity<List<BeneficiaryDetailsResponse>> getAllDetails(){
        return ResponseEntity.ok(beneficiaryService.getAllDetails());
    }


}