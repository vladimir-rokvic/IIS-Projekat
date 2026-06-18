package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.AidPackageDTO;
import com.iis.projekat.dto.Beneficiary.PackageItemDTO;
import com.iis.projekat.model.Beneficiary.AidPackage;
import com.iis.projekat.model.Beneficiary.Beneficiary;
import com.iis.projekat.model.Beneficiary.PackageItem;
import com.iis.projekat.repository.Beneficiary.AidPackageRepository;
import com.iis.projekat.repository.Beneficiary.BeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AidPackageService {

    @Autowired
    private AidPackageRepository aidPackageRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

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

        AidPackage aidPackage = new AidPackage();

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
}