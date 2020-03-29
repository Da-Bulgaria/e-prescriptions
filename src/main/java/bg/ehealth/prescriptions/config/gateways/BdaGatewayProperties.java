package bg.ehealth.prescriptions.config.gateways;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("bda.gateway")
@Valid
public class BdaGatewayProperties {

    @NotBlank
    private String host;
    @NotBlank
    private String registersUrl;
    @NotBlank
    private String registerPharmacies;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectionTimeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRegistersUrl() {
        return registersUrl;
    }

    public void setRegistersUrl(String registersUrl) {
        this.registersUrl = registersUrl;
    }

    public String getRegisterPharmacies() {
        return registerPharmacies;
    }

    public void setRegisterPharmacies(String registerPharmacies) {
        this.registerPharmacies = registerPharmacies;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
