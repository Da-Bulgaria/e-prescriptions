package bg.ehealth.prescriptions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bg.ehealth.prescriptions.persistence.DoctorRepository;
import bg.ehealth.prescriptions.persistence.PharmacistRepository;
import bg.ehealth.prescriptions.persistence.model.User;
import bg.ehealth.prescriptions.persistence.model.enums.UserType;

@Service
public class UserService {
    
    @Autowired
    private PharmacistRepository pharmacistRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    public User getUserByUin(String uin, UserType userType) {
        if (userType == UserType.PHARMACIST) {
            return pharmacistRepository.getOne(uin);
        } else if (userType == UserType.DOCTOR) {
            return doctorRepository.getOne(uin);
        }
        return null;
    }
    
    public User getUserByEmail(String email, UserType userType) {
        if (userType == UserType.PHARMACIST) {
            return pharmacistRepository.findByEmail(email);
        } else if (userType == UserType.DOCTOR) {
            return doctorRepository.findByEmail(email);
        }
        return null;
    }
}
