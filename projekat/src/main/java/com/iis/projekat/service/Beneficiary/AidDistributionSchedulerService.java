package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.model.Beneficiary.AidDistribution;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import com.iis.projekat.repository.Beneficiary.AidDistributionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AidDistributionSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(AidDistributionSchedulerService.class);

    private final AidDistributionRepository aidDistributionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private void closeDistribution(Long distributionId) {
        entityManager.createNativeQuery("CALL sp_close_distribution(?1)")
                .setParameter(1, distributionId)
                .executeUpdate();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void completeExpiredDistributions() {
        LocalDate today = LocalDate.now();

        List<AidDistribution> expired = aidDistributionRepository
                .findAllByScheduledDateLessThanEqualAndStatus(today, DistributionStatus.IN_PROGRESS);

        for (AidDistribution d : expired) {
            try {
                closeDistributionInNewTransaction(d.getId());
            } catch (Exception e) {
                log.warn("Distribucija {} nije zatvorena: {}", d.getId(), e.getMessage());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeDistributionInNewTransaction(Long distributionId) {
        entityManager.createNativeQuery("CALL sp_close_distribution(?1)")
                .setParameter(1, distributionId)
                .executeUpdate();
    }
}
