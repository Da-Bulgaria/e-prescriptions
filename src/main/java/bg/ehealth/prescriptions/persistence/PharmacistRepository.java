package bg.ehealth.prescriptions.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.ehealth.prescriptions.persistence.model.Pharmacist;

/**
 * Repository for handling users, including authentication 
 */
@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, String> {

    Pharmacist findByEmail(String email);
}
