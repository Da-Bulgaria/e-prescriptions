package bg.ehealth.prescriptions.persistence.model;

import java.util.UUID;

import javax.persistence.Entity;

@Entity
public class Doctor extends User {
    
    private UUID organizationId;
}
