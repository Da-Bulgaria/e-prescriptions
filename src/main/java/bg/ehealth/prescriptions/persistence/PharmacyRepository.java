package bg.ehealth.prescriptions.persistence;

import bg.ehealth.prescriptions.persistence.model.Pharmacy;
import bg.ehealth.prescriptions.persistence.model.PharmacyView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, String> {

    List<PharmacyView> findAllByOrderByCity();
}
