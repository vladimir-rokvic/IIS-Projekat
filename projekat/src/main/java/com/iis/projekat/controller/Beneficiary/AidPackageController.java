package com.iis.projekat.controller.Beneficiary;

import com.iis.projekat.dto.Beneficiary.AidHistoryResponse;
import com.iis.projekat.dto.Beneficiary.AidPackageDTO;
import com.iis.projekat.dto.Beneficiary.AidPackageResponse;
import com.iis.projekat.dto.Beneficiary.PackageItemResponse;
import com.iis.projekat.model.Beneficiary.AidPackage;
import com.iis.projekat.model.Beneficiary.PackageItem;
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

            AidPackage saved = aidPackageService.create(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private AidPackageResponse toResponse(AidPackage p) {
        return AidPackageResponse.builder()
                .id(p.getId())
                .beneficiaryId(p.getBeneficiary().getId())
                .beneficiaryName(p.getBeneficiary().getName())
                .items(p.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }

    private PackageItemResponse toItemResponse(PackageItem i) {
        return PackageItemResponse.builder()
                .id(i.getId())
                .product(i.getProduct())
                .quantity(i.getQuantity())
                .description(i.getDescription())
                .unit(i.getUnit())
                .build();
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<AidHistoryResponse>> getHistory(@PathVariable Long id){
        return ResponseEntity.ok(aidPackageService.getHistory(id));
    }
}