package bg.ehealth.prescriptions.gateways.cdc;

import bg.ehealth.prescriptions.config.gateways.cdc.CdcGatewayProperties;
import bg.ehealth.prescriptions.stereotype.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.io.InputStream;

@Gateway
public class CdcGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdcGateway.class);

    private final WebTarget cdcMedicineNameRegistry;

    public CdcGateway(Client cdcClient, CdcGatewayProperties properties) {
        this.cdcMedicineNameRegistry = cdcClient.target(UriComponentsBuilder
                .fromHttpUrl(properties.getHost())
                .path(properties.getPublicationsUrl())
                .path(properties.getRegisterMedicines())
                .toUriString());
    }

    public InputStream cdcMedicineRegistry() {
        LOGGER.debug("Fetching cdc non proprietary medicines registry from: {}", cdcMedicineNameRegistry.getUri());
        return cdcMedicineNameRegistry.request().get().readEntity(InputStream.class);
    }
}
