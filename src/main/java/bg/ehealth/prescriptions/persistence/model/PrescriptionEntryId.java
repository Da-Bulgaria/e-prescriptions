package bg.ehealth.prescriptions.persistence.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

/**
 * Composite primary key for prescription medicine entries
 */
@Embeddable
public class PrescriptionEntryId implements Serializable {
    
    private UUID prescriptionId;
    private String medicineAttCode;
    
    public UUID getPrescriptionId() {
        return prescriptionId;
    }
    public void setPrescriptionId(UUID prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    public String getMedicineAttCode() {
        return medicineAttCode;
    }
    public void setMedicineAttCode(String medicineAttCode) {
        this.medicineAttCode = medicineAttCode;
    }
}