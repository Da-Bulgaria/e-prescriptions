package bg.ehealth.prescriptions.services.pharmacy;

import bg.ehealth.prescriptions.persistence.model.PharmacyView;

import java.util.List;

public interface PharmacyService {

    List<PharmacyView> allPharmacies();

    void importPharmacies();
}
