package bg.ehealth.prescriptions.services.pharmacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PharmacyServiceJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(PharmacyServiceJob.class);

    private final PharmacyService service;

    public PharmacyServiceJob(PharmacyService service) {
        this.service = service;
    }

    @Scheduled(cron = "${pharmacy.registry.import.schedule:0 0 0 * * SUN}")
    public void importPharmacies() {
        LOGGER.info("Starting cron job: import pharmacy registry.");
        service.saveAll(service.seedPharmacies());
        LOGGER.info("Cron job finished.");
    }
}
