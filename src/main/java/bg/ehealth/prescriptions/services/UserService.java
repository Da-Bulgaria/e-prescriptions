package bg.ehealth.prescriptions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public User getUserByUin(String uin, UserType userType) {
        if (userType == UserType.PHARMACIST) {
            return pharmacistRepository.findById(uin).orElseThrow(() -> new BadCredentialsException("No user found"));
        } else if (userType == UserType.DOCTOR) {
            return doctorRepository.findById(uin).orElseThrow(() -> new BadCredentialsException("No user found"));
        }
        return null;
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email, UserType userType) {
        if (userType == UserType.PHARMACIST) {
            return pharmacistRepository.findByEmail(email);
        } else if (userType == UserType.DOCTOR) {
            return doctorRepository.findByEmail(email);
        }
        return null;
    }
}