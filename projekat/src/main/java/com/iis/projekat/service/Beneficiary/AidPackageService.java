package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.*;
import com.iis.projekat.model.Beneficiary.*;
import com.iis.projekat.repository.Beneficiary.AidDistributionRepository;
import com.iis.projekat.repository.Beneficiary.AidPackageRepository;
import com.iis.projekat.repository.Beneficiary.BeneficiaryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AidPackageService {

    @Autowired
    private AidPackageRepository aidPackageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;
    @Autowired
    private AidDistributionRepository aidDistributionRepository;

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public AidPackage create(AidPackageDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("DTO is null");
        }

        if (dto.getBeneficiaryId() == null) {
            throw new IllegalArgumentException("Beneficiary missing");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Items missing");
        }

        Optional<Beneficiary> opt =
                beneficiaryRepository.findById(dto.getBeneficiaryId());

        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Beneficiary not found");
        }

        Optional<AidDistribution> dis =
                aidDistributionRepository.findById(dto.getDistributionId());

        if (dis.isEmpty()) {
            throw new IllegalArgumentException("Disitribution not found");
        }

        CapacityCheckResult capacity = checkDistributionCapacity(dto.getDistributionId());

        if (!capacity.hasSpace()) {
            throw new IllegalStateException(
                    "Nema slobodnog mesta na lokaciji za distribuciju %d (dodeljeno: %d, kapacitet: %s)"
                            .formatted(
                                    dto.getDistributionId(),
                                    capacity.assignedPackages(),
                                    capacity.locationCapacity()
                            )
            );
        }

        AidPackage aidPackage = new AidPackage();

        aidPackage.setDistribution(dis.get());

        aidPackage.setBeneficiary(opt.get());

        List<PackageItem> packageItems = new ArrayList<>();

        for (PackageItemDTO itemDTO : dto.getItems()) {

            if (isBlank(itemDTO.getProduct())
                    || itemDTO.getQuantity() == null
                    || itemDTO.getQuantity() <= 0) {

                throw new IllegalArgumentException("Invalid package item");
            }

            PackageItem item = new PackageItem();

            item.setProduct(itemDTO.getProduct());
            item.setQuantity(itemDTO.getQuantity());
            item.setAidPackage(aidPackage);

            packageItems.add(item);
        }

        aidPackage.setItems(packageItems);

        return aidPackageRepository.save(aidPackage);
    }

    private CapacityCheckResult checkDistributionCapacity(Long distributionId) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM funk_distribution_capacity_check(?1)"
        );
        query.setParameter(1, distributionId);

        Object[] row = (Object[]) query.getSingleResult();

        Integer assigned = (Integer) row[0];
        Integer capacity = (Integer) row[1];
        Integer remaining = (Integer) row[2];
        Boolean hasSpace = (Boolean) row[3];

        return new CapacityCheckResult(assigned, capacity, remaining, hasSpace);
    }

    public List<AidHistoryResponse> getHistory(Long id) {

        List<AidPackage> packages = aidPackageRepository.findByBeneficiaryId(id);

        List<AidHistoryResponse> responses = new ArrayList<>();
        for(AidPackage p : packages){
            if(p.getDistribution().getStatus()== DistributionStatus.COMPLETED) {
                responses.add(ToResponse(p));
            }
        }

        return responses;
    }

    private AidHistoryResponse ToResponse(AidPackage p){
        return AidHistoryResponse.builder()
                .beneficiaryId(p.getBeneficiary().getId())
                .beneficiaryName(p.getBeneficiary().getName())
                .dateReceived(p.getDistribution().getScheduledDate())
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