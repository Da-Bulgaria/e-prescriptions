package bg.ehealth.prescriptions.services.medicine.nonproprietary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NonProprietaryMedicineNamesServiceJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonProprietaryMedicineNamesServiceJob.class);

    private final NonProprietaryMedicineNamesService service;

    public NonProprietaryMedicineNamesServiceJob(NonProprietaryMedicineNamesService service) {
        this.service = service;
    }

    @Scheduled(cron = "${medicine.names.registry.import.schedule:0 0 0 * * SUN}")
    public void importMedicineNames() {
        LOGGER.info("Starting cron job: import non-proprietary medicine names registry.");
        service.saveAll(service.seedNonProprietaryMedicineNames());
        LOGGER.info("Cron job finished.");
    }
}
