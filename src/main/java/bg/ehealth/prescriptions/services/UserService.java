package bg.ehealth.prescriptions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bg.ehealth.prescriptions.persistence.UserRepository;
import bg.ehealth.prescriptions.persistence.model.User;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User getUserByUin(String uin) {
        return userRepository.getOne(uin);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
