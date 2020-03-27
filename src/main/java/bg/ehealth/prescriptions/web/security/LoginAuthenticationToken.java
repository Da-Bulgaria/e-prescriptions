package bg.ehealth.prescriptions.web.security;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import bg.ehealth.prescriptions.persistence.model.User;

/**
 * Custom token for logging-in with email, password and potentially 2fa verification code
 */
public class LoginAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private int verificationCode;

    public LoginAuthenticationToken(User user, String password, int verificationCode) {
        super(user, password,
                Collections.singletonList(new SimpleGrantedAuthority(user.getUserType().toString())));
        this.verificationCode = verificationCode;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }

    public UserDetails getUser() {
        return (UserDetails) getPrincipal();
    }
}
