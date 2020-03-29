package bg.ehealth.prescriptions.config.gateways;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(BdaGatewayProperties.class)
public class BdaGatewayConfig {

    @Bean
    public Client bdaClient(BdaGatewayProperties properties) {
        return ClientBuilder.newBuilder()
                .connectTimeout(properties.getConnectionTimeout().getSeconds(), TimeUnit.SECONDS)
                .build();
    }
}
