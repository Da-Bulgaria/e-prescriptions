package bg.ehealth.prescriptions.persistence.model;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private int timesPerDay;
    
    @Column
    private int units;
    
    @Column
    private String icdCode;
    
    @Column
    private LocalDateTime dispensedTime;
    
    @Column
    private UUID dispensingPharmacy;
    
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
