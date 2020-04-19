package bg.ehealth.prescriptions.config.gateways.cdc;

import bg.ehealth.prescriptions.config.gateways.GatewayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties("cdc.gateway")
@Valid
public class CdcGatewayProperties extends GatewayProperties {

    @NotBlank
    private String publicationsUrl;
    @NotBlank
    private String registerMedicines;

    public String getPublicationsUrl() {
        return publicationsUrl;
    }

    public void setPublicationsUrl(String publicationsUrl) {
        this.publicationsUrl = publicationsUrl;
    }

    public String getRegisterMedicines() {
        return registerMedicines;
    }

    public void setRegisterMedicines(String registerMedicines) {
        this.registerMedicines = registerMedicines;
    }
}
