package pl.cyfronet.fid.cmdbproxy.security;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.tomakehurst.wiremock.client.WireMock;

import pl.cyfronet.fid.cmdbproxy.WireMockTest;
import pl.cyfronet.fid.cmdbproxy.pdp.Pdp;

public class OwnersValidationTest extends WireMockTest {

    @Autowired
    private Pdp pdp;

    @Test
    public void currentUserIsAddedToOwnersListForRootItem() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);
        stubFor(WireMock.put(urlEqualTo("/crud/without-owner"))
                .withRequestBody(equalToJson("{\"type\":\"provider\",\"data\":{\"name\":\"root\",\"owners\":[\"user\"]}}"))
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.CREATED.value())));


        ResponseEntity<String> response = put("/cmdb-crud/without-owner", "valid",
                "{\"type\": \"provider\", \"data\": {\"name\": \"root\"}}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void currentUserIsNotUpdatedForRootItem() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);
        stubFor(WireMock.put(urlEqualTo("/crud/with-owner"))
                .withRequestBody(equalToJson("{\"type\":\"provider\",\"data\":{\"name\":\"root\",\"owners\":[\"differentUser\"]}}"))
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.CREATED.value())));


        ResponseEntity<String> response = put("/cmdb-crud/with-owner", "valid",
                "{\"type\": \"provider\", \"data\": {\"name\": \"root\",\"owners\":[\"differentUser\"]}}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void ownersIsNotAddedIntoLeaf() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);
        stubFor(WireMock.put(urlEqualTo("/crud/not-root"))
                .withRequestBody(equalToJson("{\"type\":\"image\",\"data\":{\"name\": \"image\"}}"))
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.CREATED.value())));


        ResponseEntity<String> response = put("/cmdb-crud/not-root", "valid",
                "{\"type\":\"image\",\"data\":{\"name\": \"image\"}}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }
}
