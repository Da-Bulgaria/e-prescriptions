package bg.ehealth.prescriptions.persistence.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import bg.ehealth.prescriptions.persistence.model.enums.PrescriptionType;

@Entity
public class Prescription implements Identifiable<UUID> {
    
    @Id
    private UUID id;

    @Column
    private int code;
    
    @ManyToOne
    private Doctor prescribingDoctor;
    
    private PrescriptionType prescriptionType;
    
    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Doctor getPrescribingDoctor() {
        return prescribingDoctor;
    }

    public void setPrescribingDoctor(Doctor prescribingDoctor) {
        this.prescribingDoctor = prescribingDoctor;
    }

    public PrescriptionType getPrescriptionType() {
        return prescriptionType;
    }

    public void setPrescriptionType(PrescriptionType prescriptionType) {
        this.prescriptionType = prescriptionType;
    }
}
