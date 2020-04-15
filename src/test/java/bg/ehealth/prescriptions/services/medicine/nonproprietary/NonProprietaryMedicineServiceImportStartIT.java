package bg.ehealth.prescriptions.services.medicine.nonproprietary;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgresql")
public class NonProprietaryMedicineServiceImportStartIT {

    @Autowired
    private NonProprietaryMedicineNamesServiceJob service;

    @Test
    public void importNonProprietaryMedicineNames() {
        service.importMedicineNames();
    }
}