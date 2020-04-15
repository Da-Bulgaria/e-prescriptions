package bg.ehealth.prescriptions.services.medicine.nonproprietary;

import bg.ehealth.prescriptions.persistence.model.NonProprietaryMedicineName;

import java.io.InputStream;
import java.util.Set;

public interface MedicineNamesMedia {

    Set<NonProprietaryMedicineName> medicineNames(InputStream inputStream);
}
