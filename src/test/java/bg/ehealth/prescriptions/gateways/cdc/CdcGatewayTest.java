package bg.ehealth.prescriptions.gateways.cdc;

import bg.ehealth.prescriptions.config.gateways.cdc.CdcGatewayProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CdcGatewayTest {

    @Mock
    private Client client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void cdcMedicineRegistry() throws URISyntaxException {
        String uri = "http://exmple.com/url/medicines/";
        WebTarget mockWebTarget = mock(WebTarget.class, RETURNS_DEEP_STUBS);
        when(client.target(uri)).thenReturn(mockWebTarget);
        when(mockWebTarget.getUri()).thenReturn(new URI(uri));
        ByteArrayInputStream result = new ByteArrayInputStream("result".getBytes());
        when(mockWebTarget.request().get().readEntity(InputStream.class))
                .thenReturn(result);
        CdcGateway gateway = new CdcGateway(client, properties());
        assertThat(gateway.cdcMedicineRegistry()).isEqualTo(result);
        verify(client).target(uri);
    }

    private CdcGatewayProperties properties() {
        CdcGatewayProperties properties = new CdcGatewayProperties();
        properties.setHost("http://exmple.com");
        properties.setConnectionTimeout(Duration.of(10, ChronoUnit.SECONDS));
        properties.setPublicationsUrl("url/");
        properties.setRegisterMedicines("medicines/");
        return properties;
    }
}