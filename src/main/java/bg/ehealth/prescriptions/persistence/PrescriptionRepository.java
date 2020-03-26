package bg.ehealth.prescriptions.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bg.ehealth.prescriptions.persistence.model.Prescription;

/**
 * Repository class for handling prescriptions
 */
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
