package pl.cyfronet.fid.cmdb.requests;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.client.WireMock;

import pl.cyfronet.fid.cmdb.WireMockTest;

public class CmdbCrudAuthentication extends WireMockTest {

    @Value("${proxy.cmdb-crud.username}")
    private String username;

    @Value("${proxy.cmdb-crud.password}")
    private String password;

    @Test
    public void cmdbCrudCredentialsAreAddedToProxiedRequests() throws Exception {
        stubOKUserInfo("valid");
        stubAuthGetOk("/crud/id", "ok", username, password);

        assertThat(get("/cmdb-crud/id", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void test() throws Exception {
        stubOKUserInfo("valid");

        stubFor(WireMock.get(urlEqualTo("/no-crud"))
                .withHeader("authorization", absent())
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.OK.value())));


        assertThat(get("/cmdb/no-crud", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
