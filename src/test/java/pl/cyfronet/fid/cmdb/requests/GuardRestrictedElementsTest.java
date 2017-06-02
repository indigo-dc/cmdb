package pl.cyfronet.fid.cmdb.requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pl.cyfronet.fid.cmdb.WireMockTest;
import pl.cyfronet.fid.cmdb.pdp.Pdp;

public class GuardRestrictedElementsTest extends WireMockTest {

    @Autowired
    private Pdp pdp;

    @Before
    public void setUp() {
        stubOKUserInfo("valid", "user");
    }

    @Test
    public void cannotChangeItemType() throws Exception {
        when(pdp.canManage("user", "provider-100IT")).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/provider-100IT", "valid",
                       "{\"_rev\": \"rev\", \"type\": \"service\", \"data\": {\"owners\": [\"userC\"]}}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void cannotChangeRelationFields() throws Exception {
        when(pdp.canManage("user", "7efc59c5db69ea67c5100de0f72580ea")).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/7efc59c5db69ea67c5100de0f72580ea", "valid",
                       "{\"_id\": \"7efc59c5db69ea67c5100de0f72580ea\", "
                       + "\"_rev\": \"1-1a77cddb6627308faae8667b7e7cefc7\", "
                       + "\"type\": \"image\", \"owners\": [\"userA\"], "
                       + "\"data\": { \"service\": \"different_service\" }}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
