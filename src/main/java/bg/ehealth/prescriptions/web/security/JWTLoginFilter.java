package bg.ehealth.prescriptions.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bg.ehealth.prescriptions.persistence.model.User;
import bg.ehealth.prescriptions.persistence.model.enums.UserType;
import bg.ehealth.prescriptions.services.UserService;

/**
 * Filter that is executed for login to set JWT token as response cookie
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTLoginFilter.class);

    private final ObjectMapper mapper;

    private boolean secureCookies;
    private String jwtSecret;
    private UserService userService;

    public JWTLoginFilter(String url, AuthenticationManager authManager, boolean useHttps, 
            String jwtSecret, UserService userService) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.secureCookies = useHttps;
        this.jwtSecret = jwtSecret;
        this.userService = userService;
        this.mapper = new ObjectMapper();
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
        
        if (creds.getUserType() == null) {
            throw new BadCredentialsException("User type is mandatory");
        }
        
        User user;
        
        if (StringUtils.isNotBlank(creds.getUin())) {
            user = userService.getUserByUin(creds.getUin(), creds.getUserType());
        } else if (StringUtils.isNotBlank(creds.getEmail())){
            user = userService.getUserByEmail(creds.getEmail(), creds.getUserType());
        } else {
            throw new BadCredentialsException("Either UIN or email should be specified");
        }
        if (user == null) {
            throw new BadCredentialsException("Failed to authenticate");
        }

        return getAuthenticationManager().authenticate(
                new LoginAuthenticationToken(user, creds.getPassword(), creds.getVerificationCode()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
                                            FilterChain chain, Authentication auth) {

        // The authentication provider returns a spring security User object with username=userID
        User user = userService.getUserByUin(auth.getName(), 
                UserType.valueOf(auth.getAuthorities().iterator().next().getAuthority()));
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
        private UserType userType;
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

        public UserType getUserType() {
            return userType;
        }

        public void setUserType(UserType userType) {
            this.userType = userType;
        }
    }
}