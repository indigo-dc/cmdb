package pl.cyfronet.fid.cmdbproxy.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import pl.cyfronet.fid.cmdbproxy.WireMockTest;

public class AuthorizationFilterTest extends WireMockTest {

    @Before
    public void setUp() {
        stubOKUserInfo("valid");
    }

    @Test
    public void getAllowedForAllRequests() throws Exception {
        stubGetOk("/get", "ok");
        stubGetOk("/crud/get", "ok");

        assertThat(get("/cmdb/get", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get("/cmdb-crud/get", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void otherThanGetAllowedOnlyForCMDBCrud() throws Exception {
        stubDeleteOk("/crud/delete");

        assertThat(delete("/cmdb/delete", "valid").getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(delete("/cmdb-crud/delete", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
