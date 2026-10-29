package com.dem_ams.batch;

import com.dem_ams.domain.model.Lease;
import com.dem_ams.domain.model.enums.LeaseStatus;
import com.dem_ams.repository.LeaseRepository;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class LeaseStatusProcessor implements ItemProcessor<Lease, Lease> {

    private static final Logger log = LoggerFactory.getLogger(LeaseStatusProcessor.class);
    private final LeaseRepository leaseRepository;

    public LeaseStatusProcessor(LeaseRepository leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Override
    public @Nullable Lease process(Lease lease) throws Exception {
        LeaseStatus currentStatus = lease.getStatus();
        lease.setStatus(LeaseStatus.EXPIRED);
        LeaseStatus updatedStatus = lease.getStatus();

        log.info("Converting lease current status: ({}) into ({})", currentStatus, updatedStatus);

        return leaseRepository.save(lease);
    }
}
