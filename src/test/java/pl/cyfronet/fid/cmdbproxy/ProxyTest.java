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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;

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

        stubUserInfo();

        ResponseEntity<String> response = get("/cmdb/provider/list", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("providers list");
    }

    @Test
    public void testBearerTokenIsNotValidForCMDB() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/userinfo"))
                .withHeader("Authorization", new EqualToPattern("Bearer invalid"))
                .willReturn(aResponse()
                      .withStatus(HttpStatus.FORBIDDEN.value())));

        HttpStatus status = get("/cmdb/provider/list", "invalid").getStatusCode();
        assertThat(status).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testRequestIsProxiedIntoCMDBCrud() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/crud/id"))
                .willReturn(aResponse()
                    .withBody("document payload")
                    .withStatus(HttpStatus.OK.value())));

        stubUserInfo();

        ResponseEntity<String> response = get("/cmdb-crud/id", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("document payload");
    }

    @Test
    public void testBearerTokenIsNotValidForCMDBCrud() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/userinfo"))
                .withHeader("Authorization", new EqualToPattern("Bearer invalid"))
                .willReturn(aResponse()
                      .withStatus(HttpStatus.FORBIDDEN.value())));

        HttpStatus status = get("/cmdb-crud/id", "invalid").getStatusCode();
        assertThat(status).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private void stubUserInfo() {
        stubFor(WireMock.get(urlEqualTo("/userinfo"))
                .withHeader("Authorization", new EqualToPattern("Bearer valid"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"sub\": \"usersub\"}")
                    .withStatus(HttpStatus.OK.value())));
    }

    private ResponseEntity<String> get(String path, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(path, HttpMethod.GET, entity, String.class);
    }
}
