package bg.ehealth.prescriptions.persistence;

import bg.ehealth.prescriptions.persistence.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findAllByAtcCode(String atcCode);
}
