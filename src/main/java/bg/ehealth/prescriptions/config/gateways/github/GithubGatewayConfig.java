package bg.ehealth.prescriptions.config.gateways.github;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(GithubGatewayProperties.class)
public class GithubGatewayConfig {

    @Bean
    public Client githubClient(GithubGatewayProperties properties) {
        return ClientBuilder.newBuilder()
                .connectTimeout(properties.getConnectionTimeout().getSeconds(), TimeUnit.SECONDS)
                .build();
    }
}
