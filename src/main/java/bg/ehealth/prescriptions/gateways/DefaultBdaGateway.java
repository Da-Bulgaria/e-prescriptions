package bg.ehealth.prescriptions.gateways;

import bg.ehealth.prescriptions.config.gateways.BdaGatewayProperties;
import bg.ehealth.prescriptions.stereotype.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Gateway
public class DefaultBdaGateway implements BdaGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBdaGateway.class);

    private final WebTarget pharmacyRegistry;

    public DefaultBdaGateway(Client bdaClient, BdaGatewayProperties properties) {
        this.pharmacyRegistry = bdaClient.target(UriComponentsBuilder
                        .fromHttpUrl(properties.getHost())
                        .path(properties.getRegistersUrl())
                        .path(properties.getRegisterPharmacies())
                        .toUriString());
    }

    @Override
    public InputStream pharmacyRegistry() {
        LOGGER.debug("Fetching pharmacy registry from: {}", pharmacyRegistry.getUri());
        return pharmacyRegistry.request(MediaType.TEXT_PLAIN_TYPE).get().readEntity(InputStream.class);
    }
}
