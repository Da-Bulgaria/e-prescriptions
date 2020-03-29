package bg.ehealth.prescriptions.services;

import bg.ehealth.prescriptions.services.pharmacy.PharmacyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PharmacyServiceImportStartIT {

    @Autowired
    private PharmacyService service;

    @Test
    public void importPharmacyRegistry() {
        service.importPharmacies();
    }
}