package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidDistribution;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import com.iis.projekat.repository.Beneficiary.AidDistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AidDistributionSchedulerService {

    private final AidDistributionRepository aidDistributionRepository;

    @Scheduled(cron = "0 0 0 * * *") // Svaki dan u ponoć
    @Transactional
    public void completeExpiredDistributions() {
        LocalDate today = LocalDate.now();

        List<AidDistribution> expired = aidDistributionRepository
                .findAllByScheduledDateLessThanEqualAndStatus(today, DistributionStatus.PLANNED);

        expired.forEach(d -> d.setStatus(DistributionStatus.COMPLETED));

        aidDistributionRepository.saveAll(expired);
    }
}
