package com.iis.projekat.dto.Beneficiary;

public record CapacityCheckResult(
        Integer assignedPackages,
        Integer locationCapacity,
        Integer remainingCapacity,
        boolean hasSpace
) {}
