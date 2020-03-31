package bg.ehealth.prescriptions.services.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MedicineServiceJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicineServiceJob.class);

    private final MedicineService service;

    public MedicineServiceJob(MedicineService service) {
        this.service = service;
    }

    @Scheduled(cron = "${medicine.registry.import.schedule:0 0 0 * * SUN}")
    public void start() {
        LOGGER.info("Starting cron job: import medicines registry.");
        service.importMedicines();
        LOGGER.info("Cron job finished.");
    }
}
