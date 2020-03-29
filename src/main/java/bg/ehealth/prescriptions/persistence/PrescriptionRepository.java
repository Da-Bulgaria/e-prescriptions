package bg.ehealth.prescriptions.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.ehealth.prescriptions.persistence.model.Prescription;

/**
 * Repository class for handling prescriptions
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
