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

    //
    // Create new elements
    //

    @Test
    public void currentUserIsAddedToOwnersListForRootItem() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);
        stubFor(WireMock.put(urlEqualTo("/crud/without-owner"))
                .withRequestBody(equalToJson("{\"type\":\"provider\",\"owners\":[\"user\"],\"data\":{\"name\":\"root\"}}"))
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.CREATED.value())));


        ResponseEntity<String> response = put("/cmdb-crud/without-owner", "valid",
                "{\"type\": \"provider\", \"data\": {\"name\": \"root\"}}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void currentUserIsNotAddedForRootItemWhenOwnersListIsNotEmpty() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);
        stubFor(WireMock.put(urlEqualTo("/crud/with-owner"))
                .withRequestBody(equalToJson("{\"type\":\"provider\",\"owners\":[\"differentUser\"],\"data\":{\"name\":\"root\"}}"))
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.CREATED.value())));


        ResponseEntity<String> response = put("/cmdb-crud/with-owner", "valid",
                "{\"type\": \"provider\",\"owners\":[\"differentUser\"], \"data\": {\"name\": \"root\"}}");

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

    //
    // Updating elements
    //

    @Test
    public void rootItemRequireAtLeastOneOwner() throws Exception {
       stubOKUserInfo("valid", "user");
       when(pdp.canManage("user", "provider-100IT")).thenReturn(true);


       ResponseEntity<String> response = put("/cmdb-crud/provider-100IT", "valid",
               "{\"type\":\"provider\", \"_rev\": \"1-cc1726a91bc298e912d20ac2f15ec58e\", \"data\":{\"name\": \"provider\"}}");

       assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void leafOwnersListIsNotRequired() throws Exception {
       stubOKUserInfo("valid", "user");
       when(pdp.canManage("user", "leaf")).thenReturn(true);
       stubFor(WireMock.put(urlEqualTo("/crud/leaf"))
               .willReturn(aResponse()
                   .withBody("ok")
                   .withStatus(HttpStatus.OK.value())));

       ResponseEntity<String> response = put("/cmdb-crud/leaf", "valid",
               "{\"type\":\"service\", \"_rev\": \"rev\", \"data\":{\"name\": \"service\"}}");

       assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
       assertThat(response.getBody()).isEqualTo("ok");
    }
}
