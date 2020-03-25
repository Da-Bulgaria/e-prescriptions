package bg.ehealth.prescriptions.persistence;

import bg.ehealth.prescriptions.persistence.model.DummyModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyRepository extends JpaRepository<DummyModel, Long> {
}
