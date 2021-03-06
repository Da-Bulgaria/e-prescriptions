package bg.ehealth.prescriptions.services;

import bg.ehealth.prescriptions.services.pharmacy.PharmacyService;
import bg.ehealth.prescriptions.services.pharmacy.PharmacyServiceJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgresql")
public class PharmacyServiceImportStartIT {

    @Autowired
    private PharmacyServiceJob service;

    @Test
    public void importPharmacyRegistry() {
        service.importPharmacies();
    }
}