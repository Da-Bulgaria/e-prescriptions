package bg.ehealth.prescriptions.gateways.github;

import bg.ehealth.prescriptions.config.gateways.github.GithubGatewayProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GithubGatewayTest {

    @Mock
    private Client client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void pdTableFile() throws URISyntaxException, IOException {
        String result = "result";
        String uri = "http://exmple.com/path/";
        WebTarget mockWebTarget = mock(WebTarget.class, RETURNS_DEEP_STUBS);
        when(client.target(uri)).thenReturn(mockWebTarget);
        when(mockWebTarget.getUri()).thenReturn(new URI(uri));
        when(mockWebTarget.request().get().readEntity(String.class)).thenReturn(result);
        GithubGateway gateway = new GithubGateway(client, properties());
        assertThat(new String(gateway.pdTableContent().readAllBytes())).isEqualTo(result);
        verify(client).target(uri);
    }

    private GithubGatewayProperties properties() {
        GithubGatewayProperties properties = new GithubGatewayProperties();
        properties.setHost("http://exmple.com");
        properties.setConnectionTimeout(Duration.of(10, ChronoUnit.SECONDS));
        properties.setPath("path/");
        return properties;
    }
}