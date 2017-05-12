package pl.cyfronet.fid.cmdbproxy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

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
public abstract class WireMockTest {

    @Autowired
    private TestRestTemplate restTemplate;

    protected void stubOKUserInfo(String bearerValue) {
        stubOKUserInfo(bearerValue, "usersub");
    }

    protected void stubOKUserInfo(String bearerValue, String userSub) {
        stubFor(WireMock.get(urlEqualTo("/userinfo"))
                .withHeader("Authorization", new EqualToPattern("Bearer " + bearerValue))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sub\": \"" + userSub + "\"}").withStatus(HttpStatus.OK.value())));
    }

    protected void stubBadUserInfo(String bearerValue) {
        stubFor(WireMock.get(urlEqualTo("/userinfo"))
                .withHeader("Authorization", new EqualToPattern("Bearer " + bearerValue))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.FORBIDDEN.value())));
    }

    protected ResponseEntity<String> get(String path, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(path, HttpMethod.GET, entity, String.class);
    }

    protected ResponseEntity<String> post(String path, String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(path, HttpMethod.POST, entity, String.class);
    }

    protected ResponseEntity<String> put(String path, String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(path, HttpMethod.PUT, entity, String.class);
    }

    protected ResponseEntity<String> delete(String path, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(path, HttpMethod.DELETE, entity, String.class);
    }

    protected void stubGetOk(String path, String responseBody) {
        stubFor(WireMock.get(urlEqualTo(path))
                .willReturn(aResponse()
                    .withBody(responseBody)
                    .withStatus(HttpStatus.OK.value())));
    }

    protected void stubAuthGetOk(String path, String responseBody, String username, String password) {
        stubFor(WireMock.get(urlEqualTo(path))
                .withBasicAuth(username, password)
                .willReturn(aResponse()
                    .withBody(responseBody)
                    .withStatus(HttpStatus.OK.value())));
    }

    protected void stubPostOk(String path, String body) {
        stubFor(WireMock.post(urlEqualTo(path))
                .willReturn(aResponse()
                    .withBody(body)
                    .withStatus(HttpStatus.CREATED.value())));
    }

    protected void stubPutOk(String path, String body) {
        stubFor(WireMock.put(urlEqualTo(path))
                .willReturn(aResponse()
                    .withBody(body)
                    .withStatus(HttpStatus.OK.value())));
    }

    protected void stubDeleteOk(String path) {
        stubFor(WireMock.delete(urlEqualTo(path))
                .willReturn(aResponse().withStatus(HttpStatus.NO_CONTENT.value())));
    }
}
