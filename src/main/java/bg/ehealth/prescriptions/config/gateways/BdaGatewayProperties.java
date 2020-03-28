package bg.ehealth.prescriptions.config.gateways;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("bda.gateway")
@ConstructorBinding
@Validated
public class BdaGatewayProperties {

    @NotBlank
    private final String host;
    @NotBlank
    private final String registersUrl;
    @NotBlank
    private final String registerPharmacies;
    @DurationUnit(ChronoUnit.SECONDS)
    private final Duration connectionTimeout;

    public BdaGatewayProperties(String host, String registersUrl, String registerPharmacies,
                                @DefaultValue("60s") Duration connectionTimeout) {
        this.host = host;
        this.registersUrl = registersUrl;
        this.registerPharmacies = registerPharmacies;
        this.connectionTimeout = connectionTimeout;
    }

    public String getHost() {
        return host;
    }

    public String getRegistersUrl() {
        return registersUrl;
    }

    public String getRegisterPharmacies() {
        return registerPharmacies;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }
}
