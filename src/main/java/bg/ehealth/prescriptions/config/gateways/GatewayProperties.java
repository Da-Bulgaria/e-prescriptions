package bg.ehealth.prescriptions.config.gateways;

import org.springframework.boot.convert.DurationUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Valid
public class GatewayProperties {

    @NotBlank
    private String host;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectionTimeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
