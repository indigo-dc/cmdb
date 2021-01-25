package pl.cyfronet.fid.cmdb;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest extends WireMockTest {

    @Test
    public void testRequestIsProxedIntoCMDB() throws Exception {
        stubGetOk("/provider/list", "providers list");
        stubOKUserInfo("valid");

        ResponseEntity<String> response = get("/cmdb/_design/schema/_rewrite/provider/list", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("providers list");
    }

    public void testBearerTokenIsNotValidForCMDB() throws Exception {
        stubBadUserInfo("invalid");


        HttpStatus status = get("/cmdb/provider/list", "invalid").getStatusCode();
        assertThat(status).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testRequestIsProxiedIntoCMDBCrud() throws Exception {
        stubGetOk("/crud/id", "document payload");
        stubOKUserInfo("valid");

        ResponseEntity<String> response = get("/cmdb-crud/id", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("document payload");
    }

    public void testBearerTokenIsNotValidForCMDBCrud() throws Exception {
        stubBadUserInfo("invalid");

        HttpStatus status = get("/cmdb-crud/id", "invalid").getStatusCode();
        assertThat(status).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testDeleteOwnedResource() throws Exception {
        stubOKUserInfo("valid");


    }

}
