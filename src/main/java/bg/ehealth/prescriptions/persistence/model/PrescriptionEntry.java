package bg.ehealth.prescriptions.persistence.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import bg.ehealth.prescriptions.persistence.model.enums.MedicinePrescriptionStatus;

@Entity
public class PrescriptionEntry implements Identifiable<PrescriptionEntryId> {

    @EmbeddedId
    private PrescriptionEntryId id;
    
    @Column
    private MedicinePrescriptionStatus status;
    
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
    
    public MedicinePrescriptionStatus getStatus() {
        return status;
    }

    public void setStatus(MedicinePrescriptionStatus status) {
        this.status = status;
    }
}
