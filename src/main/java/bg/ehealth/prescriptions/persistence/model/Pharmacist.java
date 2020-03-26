package bg.ehealth.prescriptions.persistence.model;

import java.util.UUID;

import javax.persistence.Entity;

@Entity
public class Pharmacist extends User {
    
    private UUID pharmacyId;

    public UUID getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(UUID pharmacyId) {
        this.pharmacyId = pharmacyId;
    }
    
    
}
