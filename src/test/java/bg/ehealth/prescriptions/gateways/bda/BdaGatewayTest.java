package bg.ehealth.prescriptions.gateways.bda;

import bg.ehealth.prescriptions.config.gateways.bda.BdaGatewayProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BdaGatewayTest {

    @Mock
    private Client client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void pharmacyRegistry() throws URISyntaxException {
        String uri = "http://exmple.com/url/pharmacies/";
        WebTarget mockWebTarget = mock(WebTarget.class, RETURNS_DEEP_STUBS);
        when(client.target(uri)).thenReturn(mockWebTarget);
        when(mockWebTarget.getUri()).thenReturn(new URI(uri));
        ByteArrayInputStream result = new ByteArrayInputStream("result".getBytes());
        when(mockWebTarget.request(MediaType.TEXT_PLAIN_TYPE).get().readEntity(InputStream.class))
                .thenReturn(result);
        BdaGateway gateway = new BdaGateway(client, properties());
        assertThat(gateway.pharmacyRegistry()).isEqualTo(result);
        verify(client).target(uri);
    }

    private BdaGatewayProperties properties() {
        BdaGatewayProperties properties = new BdaGatewayProperties();
        properties.setHost("http://exmple.com");
        properties.setConnectionTimeout(Duration.of(10, ChronoUnit.SECONDS));
        properties.setRegistersUrl("url/");
        properties.setRegisterPharmacies("pharmacies/");
        return properties;
    }

}