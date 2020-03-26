package bg.ehealth.prescriptions.persistence.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import bg.ehealth.prescriptions.persistence.model.enums.MedicineStatus;

@Entity
public class PrescriptionEntry implements Identifiable<PrescriptionEntryId> {

    @EmbeddedId
    private PrescriptionEntryId id;
    
    @Column
    private MedicineStatus status;
    
    @Column
    private LocalDateTime dispensedTime;
    
    @Override
    public PrescriptionEntryId getId() {
        return id;
    }

    public void setId(PrescriptionEntryId id) {
        this.id = id;
    }
    
    public MedicineStatus getStatus() {
        return status;
    }

    public void setStatus(MedicineStatus status) {
        this.status = status;
    }
}
