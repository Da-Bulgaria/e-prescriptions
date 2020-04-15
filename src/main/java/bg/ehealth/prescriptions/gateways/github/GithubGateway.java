package bg.ehealth.prescriptions.gateways.github;

import bg.ehealth.prescriptions.config.gateways.github.GithubGatewayProperties;
import bg.ehealth.prescriptions.stereotype.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Gateway
public class GithubGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubGateway.class);

    private final WebTarget pdTableFile;

    public GithubGateway(Client githubClient, GithubGatewayProperties properties) {
        this.pdTableFile = githubClient.target(UriComponentsBuilder
                .fromHttpUrl(properties.getHost())
                .path(properties.getPath())
                .toUriString());
    }

    public InputStream pdTableContent() {
        LOGGER.debug("Fetching pd table file from: {}", pdTableFile.getUri());
        return new ByteArrayInputStream(pdTableFile.request().get().readEntity(String.class).getBytes());
    }
}
