package pl.cyfronet.fid.cmdb.requests;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import pl.cyfronet.fid.cmdb.WireMockTest;

import static org.assertj.core.api.Assertions.assertThat;

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
        stubAuthGetOk("/no-crud", "ok", username, password);


        assertThat(get("/cmdb/_design/schema/_rewrite/no-crud", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
