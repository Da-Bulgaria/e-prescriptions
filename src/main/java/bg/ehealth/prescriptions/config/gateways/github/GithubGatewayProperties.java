package bg.ehealth.prescriptions.config.gateways.github;

import bg.ehealth.prescriptions.config.gateways.GatewayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties("github.gateway")
@Valid
public class GithubGatewayProperties extends GatewayProperties {

    @NotBlank
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
