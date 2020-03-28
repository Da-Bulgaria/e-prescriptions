package bg.ehealth.prescriptions.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.ehealth.prescriptions.persistence.model.User;

/**
 * Repository for handling users, including authentication 
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);
}
