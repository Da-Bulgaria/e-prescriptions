package bg.ehealth.prescriptions.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DummyProperties.class)
public class Beans {
    
}
