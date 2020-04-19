package bg.ehealth.prescriptions.config.gateways.bda;

import bg.ehealth.prescriptions.config.gateways.GatewayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties("bda.gateway")
@Valid
public class BdaGatewayProperties extends GatewayProperties {

    @NotBlank
    private String registersUrl;
    @NotBlank
    private String registerPharmacies;

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
}
