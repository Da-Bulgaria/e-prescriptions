package bg.ehealth.prescriptions.web.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import bg.ehealth.prescriptions.persistence.UserRepository;
import bg.ehealth.prescriptions.persistence.model.User;

/**
 * Spring security authentication provider that calls the data storage to retrieve user data
 */
@Component
public class PostgresAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleAuthenticator googleAuthenticator;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        if (!BCrypt.checkpw(authentication.getCredentials().toString(), userDetails.getPassword())) {
            logger.info("Failed authentication for user " + userDetails.getUsername());
            throw new BadCredentialsException("Failed to authenticate");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        if (!(authentication instanceof LoginAuthenticationToken)) {
            throw new BadCredentialsException("Invalid LoginAuthenticationToken");
        }
        LoginAuthenticationToken loginToken = (LoginAuthenticationToken) authentication;

        User user = userRepository.getOne(loginToken.getUser().getId());
        if (user == null) {
            new BadCredentialsException("Failed to find user for id=" + loginToken.getUser().getId());
        }

        if (user.getTwoFactorAuthSecret() != null
                && !googleAuthenticator.authorize(user.getTwoFactorAuthSecret(), loginToken.getVerificationCode())) {
            throw new BadCredentialsException("Missing two-factor authentication verification code");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword(),
                Collections.emptyList());
    }

}
