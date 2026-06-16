package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.*;
import com.iis.projekat.model.Beneficiary.*;
import com.iis.projekat.model.Volunteer;
import com.iis.projekat.repository.Beneficiary.AidDistributionRepository;
import com.iis.projekat.repository.Beneficiary.AidPackageRepository;
import com.iis.projekat.repository.Beneficiary.BeneficiaryRepository;
import com.iis.projekat.repository.Beneficiary.DistributionLocationRepository;
import com.iis.projekat.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AidDistributionService {

    private final AidDistributionRepository distributionRepository;
    private final DistributionLocationRepository locationRepository;
    private final VolunteerRepository volunteerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final AidPackageRepository packageRepository;

    // ── CREATE ─────────────────────────────────────────────────────────────

    public AidDistributionResponse create(AidDistributionRequest request) {
        DistributionLocation location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        List<Volunteer> volunteers = volunteerRepository.findAllById(request.getVolunteerIds());

        AidDistribution distribution = AidDistribution.builder()
                .scheduledDate(request.getScheduledDate())
                .note(request.getNote())
                .status(DistributionStatus.PLANNED)
                .location(location)
                .volunteers(volunteers)
                .build();

        return toResponse(distributionRepository.save(distribution));
    }

    // ── READ ───────────────────────────────────────────────────────────────

    public AidDistributionResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<AidDistributionResponse> getAll() {
        return distributionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────

    public AidDistributionResponse update(Long id, AidDistributionRequest request) {
        AidDistribution distribution = findOrThrow(id);

        DistributionLocation location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        List<Volunteer> volunteers = volunteerRepository.findAllById(request.getVolunteerIds());

        distribution.setScheduledDate(request.getScheduledDate());
        distribution.setNote(request.getNote());
        distribution.setLocation(location);
        distribution.setVolunteers(volunteers);

        return toResponse(distributionRepository.save(distribution));
    }

    public AidDistributionResponse updateStatus(Long id, DistributionStatus status) {
        AidDistribution distribution = findOrThrow(id);
        distribution.setStatus(status);
        return toResponse(distributionRepository.save(distribution));
    }

    // ── DELETE ─────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        AidDistribution distribution = findOrThrow(id);
        // cascade = CascadeType.ALL + orphanRemoval = true na packages
        // automatski brise AidPackage i PackageItem
        distributionRepository.delete(distribution);
    }

    // ── PACKAGES ───────────────────────────────────────────────────────────

    @Transactional
    public AidDistributionResponse addPackage(Long distributionId, AidPackageRequest request) {
        AidDistribution distribution = findOrThrow(distributionId);

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new EntityNotFoundException("Beneficiary not found"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Package must contain at least one item");
        }
        if (request.getItems().size() > 3) {
            throw new IllegalArgumentException("Package cannot contain more than 3 items");
        }

        AidPackage aidPackage = AidPackage.builder()
                .beneficiary(beneficiary)
                .distribution(distribution)
                .build();

        List<PackageItem> items = request.getItems().stream()
                .map(i -> PackageItem.builder()
                        .product(i.getProduct())
                        .quantity(i.getQuantity())
                        .description(i.getDescription())
                        .unit(i.getUnit())
                        .aidPackage(aidPackage)
                        .build())
                .toList();

        aidPackage.setItems(items);
        distribution.getPackages().add(aidPackage);

        return toResponse(distributionRepository.save(distribution));
    }

    @Transactional
    public AidDistributionResponse removePackage(Long distributionId, Long packageId) {
        AidDistribution distribution = findOrThrow(distributionId);

        AidPackage aidPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found"));

        if (!aidPackage.getDistribution().getId().equals(distributionId)) {
            throw new IllegalArgumentException("Package does not belong to this distribution");
        }

        distribution.getPackages().remove(aidPackage);
        return toResponse(distributionRepository.save(distribution));
    }

    // ── HELPERS ────────────────────────────────────────────────────────────

    private AidDistribution findOrThrow(Long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AidDistribution not found"));
    }

    private AidDistributionResponse toResponse(AidDistribution d) {
        return AidDistributionResponse.builder()
                .id(d.getId())
                .scheduledDate(d.getScheduledDate())
                .note(d.getNote())
                .status(d.getStatus())
                .location(toLocationResponse(d.getLocation()))
                .volunteers(d.getVolunteers().stream().map(this::toVolunteerResponse).toList())
                .packages(d.getPackages().stream().map(this::toPackageResponse).toList())
                .build();
    }

    private DistributionLocationResponse toLocationResponse(DistributionLocation l) {
        return DistributionLocationResponse.builder()
                .id(l.getId())
                .name(l.getName())
                .capacity(l.getCapacity())
                .type(l.getType())
                .contactName(l.getContactName())
                .contactNumber(l.getContactNumber())
                .workHoursBegin(l.getWorkHoursBegin())
                .workHoursEnd(l.getWorkHoursEnd())
                .build();
    }

    private VolunteerResponse toVolunteerResponse(Volunteer v) {
        return VolunteerResponse.builder()
                .id(v.getId())
                .name(v.getName())
                .surname(v.getSurname())
                .build();
    }

    private AidPackageResponse toPackageResponse(AidPackage p) {
        return AidPackageResponse.builder()
                .id(p.getId())
                .beneficiaryId(p.getBeneficiary().getId())
                .beneficiaryName(p.getBeneficiary().getName() + " " + p.getBeneficiary().getSurname())
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
}