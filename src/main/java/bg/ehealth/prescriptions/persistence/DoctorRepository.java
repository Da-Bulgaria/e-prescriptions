package bg.ehealth.prescriptions.persistence;

import bg.ehealth.prescriptions.persistence.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for handling users, including authentication 
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Doctor findByEmail(String email);
}
