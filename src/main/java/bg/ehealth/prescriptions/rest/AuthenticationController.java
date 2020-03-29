package bg.ehealth.prescriptions.rest;

import bg.ehealth.prescriptions.web.security.JWTLoginFilter.AccountCredentials;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    @PostMapping("/login")
    public String login(@RequestBody AccountCredentials credentials) {
        // placeholder method - it is implemented in a spring security filter - JWTLoginFilter
        return "success";
    }

    @PostMapping("/api/token")
    public String getToken(@RequestBody AccountCredentials credentials) {
        // TODO return token
        return "success";
    }

    @PostMapping("/requires2fa")
    public boolean requiresTwoFactorAuth(@RequestParam("emailOrUin") String emailOrUin) {
        // TODO
        return false;
    }
}
