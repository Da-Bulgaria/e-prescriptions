package bg.ehealth.prescriptions.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bg.ehealth.prescriptions.persistence.model.User;
import bg.ehealth.prescriptions.services.UserService;
import bg.ehealth.prescriptions.web.security.JWTLoginFilter.AccountCredentials;

@RestController
public class AuthenticationController {
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody AccountCredentials credentials) {
        // placeholder method - it is implemented in a spring security filter - JWTLoginFilter
        return "success";
    }

    @RequestMapping(value = "/api/token", method = RequestMethod.POST)
    public String getToken(@RequestBody AccountCredentials credentials) {
        // TODO return token
        return "success";
    }
    
    @RequestMapping(value = "/requires2fa", method = RequestMethod.POST)
    @ResponseBody
    public boolean requiresTwoFactorAuth(@RequestParam("emailOrUin") String emailOrUin) {
        // TODO
        return false;
    }
}
