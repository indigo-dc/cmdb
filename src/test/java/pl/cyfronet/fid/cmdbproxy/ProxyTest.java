package pl.cyfronet.fid.cmdbproxy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.client.WireMock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class ProxyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRequestIsProxedIntoCMDB() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/provider/list"))
                .willReturn(aResponse()
                    .withBody("providers list")
                    .withStatus(HttpStatus.OK.value())));

        String body = this.restTemplate.getForObject("/cmdb/provider/list", String.class);
        assertThat(body).isEqualTo("providers list");
    }
}
