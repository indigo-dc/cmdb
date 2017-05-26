package pl.cyfronet.fid.cmdbproxy.requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pl.cyfronet.fid.cmdbproxy.WireMockTest;
import pl.cyfronet.fid.cmdbproxy.pdp.Pdp;

public class GuardRestrictedElementsTest extends WireMockTest {

    @Autowired
    private Pdp pdp;

    @Test
    public void cannotChangeItemType() throws Exception {
        // as an item owner
        stubOKUserInfo("valid", "user");
        when(pdp.canManage("user", "provider-100IT")).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/provider-100IT", "valid",
                       "{\"_rev\": \"rev\", \"type\": \"service\", \"data\": {\"owners\": [\"userC\"]}}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
