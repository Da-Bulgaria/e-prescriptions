package bg.ehealth.prescriptions.services;

import bg.ehealth.prescriptions.persistence.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bg.ehealth.prescriptions.persistence.DoctorRepository;
import bg.ehealth.prescriptions.persistence.PharmacistRepository;
import bg.ehealth.prescriptions.persistence.model.User;
import bg.ehealth.prescriptions.persistence.model.enums.UserType;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PharmacistRepository pharmacistRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
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

    // fixme -> за тест само после го изтрий
    public Doctor getDoctorByUin (String uin){
        Doctor doctor = null;

        try {
            doctor = doctorRepository.findById(uin).orElse(null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return doctor;
    }
}