package bg.ehealth.prescriptions.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.warrenstrange.googleauth.GoogleAuthenticator;

@Configuration
@EnableConfigurationProperties(DummyProperties.class)
public class Beans {
    
    @Bean
    public GoogleAuthenticator googleAuthenticator() {
        return new com.warrenstrange.googleauth.GoogleAuthenticator();
    }
    
}
