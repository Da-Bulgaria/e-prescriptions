package bg.ehealth.prescriptions.web.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bg.ehealth.prescriptions.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Filter that is executed for login to set JWT token as response cookie
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTLoginFilter.class);

    private ObjectMapper mapper = new ObjectMapper();

    private boolean secureCookies;
    private String jwtSecret;

    @SuppressWarnings("checkstyle:parameternumber")
    public JWTLoginFilter(String url, AuthenticationManager authManager, boolean useHttps, String jwtSecret) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.secureCookies = useHttps;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws IOException {

       // TODO implement blocking after certian login attempts

        AccountCredentials creds;
        try {
            creds = mapper.readValue(req.getInputStream(), AccountCredentials.class);
        } catch (JsonProcessingException ex) {
            throw new BadCredentialsException("Failed to authenticate due to incorrect JSON request");
        }

        logger.debug("Attempting authentication for email={} or uin={}", creds.getEmail(), creds.getUin());
        User user = null; // TODO fetch by either email or UIN userService.getUserDetailsByEmail(creds.getEmail());
        if (user == null) {
            throw new BadCredentialsException("Failed to authenticate");
        }

        return getAuthenticationManager().authenticate(
                new LoginAuthenticationToken(user, creds.getPassword(), creds.getVerificationCode()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
                                            FilterChain chain, Authentication auth) throws IOException, ServletException {

        // The authentication provider returns a spring security User object with username=userID
        User user = null; // TODO userService.getUserDetailsById(UUID.fromString(auth.getName()));
        TokenAuthenticationService.addAuthentication(req, res, user, secureCookies, jwtSecret);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        // TODO increment unsuccessful logins counter
        super.unsuccessfulAuthentication(request, response, failed);
    }

    /**
     * Simple DTO for holding account login credentials
     */
    public static class AccountCredentials {
        private String email;
        private String uin;
        private String password;
        private int verificationCode;

        public String getUin() {
            return uin;
        }

        public void setUin(String uin) {
            this.uin = uin;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String username) {
            this.email = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(int verificationCode) {
            this.verificationCode = verificationCode;
        }
    }
}