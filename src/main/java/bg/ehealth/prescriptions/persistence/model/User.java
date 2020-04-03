package bg.ehealth.prescriptions.persistence.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import bg.ehealth.prescriptions.persistence.model.enums.UserType;

/**
 * Base class for users of the system
 */
@MappedSuperclass
public abstract class User implements Identifiable<String> {

    @Id
    private String uin;
    
    @Column
    private String email;
    
    @Column
    private String password;
    
    @Column
    private String firstName;
    
    @Column
    private String middleName;
    
    @Column
    private String lastName;
    
    @Column
    private String twoFactorAuthSecret;

    @Column
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    public String getId() {
        return uin;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTwoFactorAuthSecret() {
        return twoFactorAuthSecret;
    }

    public void setTwoFactorAuthSecret(String twoFactorAuthSecret) {
        this.twoFactorAuthSecret = twoFactorAuthSecret;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
