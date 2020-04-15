package bg.ehealth.prescriptions.persistence;

import bg.ehealth.prescriptions.persistence.model.NonProprietaryMedicineName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NonProprietaryMedicineNameRepository extends JpaRepository<NonProprietaryMedicineName, Long> {
}
