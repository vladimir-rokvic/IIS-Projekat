package com.iis.projekat.controller.Beneficiary;

import com.iis.projekat.dto.Beneficiary.*;
import com.iis.projekat.model.Beneficiary.DistributionLocation;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import com.iis.projekat.service.Beneficiary.AidDistributionService;
import com.iis.projekat.service.Beneficiary.DistributionLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribution")
@RequiredArgsConstructor
public class DistributionController {

    private final AidDistributionService distributionService;

    private final DistributionLocationService distributionLocationService;

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

    @GetMapping("/locations")
    public ResponseEntity<List<DistributionLocationResponse>> getAllLocations(){
        return ResponseEntity.ok(distributionLocationService.getAll());
    }

    @DeleteMapping("/location/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        distributionLocationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<AidDistributionResponse> create(@RequestBody AidDistributionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distributionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AidDistributionResponse>> getAll() {
        return ResponseEntity.ok(distributionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AidDistributionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(distributionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AidDistributionResponse> update(
            @PathVariable Long id,
            @RequestBody AidDistributionRequest request) {
        return ResponseEntity.ok(distributionService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AidDistributionResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam DistributionStatus status) {
        return ResponseEntity.ok(distributionService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        distributionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //package

    @PostMapping("/{id}/packages")
    public ResponseEntity<AidDistributionResponse> addPackage(
            @PathVariable Long id,
            @RequestBody AidPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distributionService.addPackage(id, request));
    }

    @DeleteMapping("/{id}/packages/{packageId}")
    public ResponseEntity<AidDistributionResponse> removePackage(
            @PathVariable Long id,
            @PathVariable Long packageId) {
        return ResponseEntity.ok(distributionService.removePackage(id, packageId));
    }
}