package com.dem_ams.task;

import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.enums.LeaseStatus;
import com.dem_ams.repository.LeaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SweepExpiredLeaseStatuses {

    private final LeaseRepository leaseRepository;
    private static final Logger log = LoggerFactory.getLogger(SweepExpiredLeaseStatuses.class);
    private static final String DAILY = "0 0 0 * * *";

    public SweepExpiredLeaseStatuses(LeaseRepository leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Scheduled(cron = DAILY)
    @Transactional
    public void setSelectLeasesToExpired() {
        int expiredCount = leaseRepository.expireLeases();
        log.info("Set expired leases status to EXPIRED. Leases set to expired ({})", expiredCount);
    }

}
