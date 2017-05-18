package pl.cyfronet.fid.cmdbproxy.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pl.cyfronet.fid.cmdbproxy.WireMockTest;
import pl.cyfronet.fid.cmdbproxy.pdp.Pdp;

public class AuthorizationFilterTest extends WireMockTest {

    private static final String CREATE_REQUEST_BODY =
            "{\"type\": \"image\", \"data\": {\"service\": \"4401ac5dc8cfbbb737b0a025758cf045\"}}";
    private static final String UPDATE_REQUEST_BODY =
            "{\"_rev\": \"rev\", \"type\": \"image\", \"data\": {\"service\": \"4401ac5dc8cfbbb737b0a025758cf045\", \"owners\": [\"user\"]}}";

    @Autowired
    private Pdp pdp;

    @Before
    public void setUp() {
        stubOKUserInfo("valid", "user");
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
        when(pdp.canManage("user", "delete")).thenReturn(true);

        assertThat(delete("/cmdb/delete", "valid").getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(delete("/cmdb-crud/delete", "valid").getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    //
    // POST - Create
    //

    @Test
    public void parentOwnerCanCreateChild() throws Exception {
        stubPostOk("/crud", "ok");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);

        ResponseEntity<String> response = post("/cmdb-crud", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void nonParentOwnerCannotCreateChild() throws Exception {
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(false);

        ResponseEntity<String> response = post("/cmdb-crud", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // PUT - Create with defined id
    //

    @Test
    public void parentOwnerCanCreateChildWithNamedId() throws Exception {
        stubPutOk("/crud/custom-id", "ok");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/custom-id", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void nonParentOwnerCannotCreateChildWithNamedId() throws Exception {
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(false);

        ResponseEntity<String> response = put("/cmdb-crud/custom-id", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // PUT - Update existing item
    //

    @Test
    public void itemOwnerCanModifyIt() throws Exception {
        stubPutOk("/crud/existing-item", "ok");
        when(pdp.canManage("user", "existing-item")).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/existing-item", "valid", UPDATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void itemNotOwnerCannotModifyIt() throws Exception {
        when(pdp.canManage("user", "existing-item")).thenReturn(false);

        ResponseEntity<String> response = put("/cmdb-crud/existing-item", "valid", UPDATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // DELETE - Remove existing item
    //

    @Test
    public void itemOwnerCanDeleteIt() throws Exception {
        stubDeleteOk("/crud/existing-item");
        when(pdp.canManage("user", "existing-item")).thenReturn(true);

        ResponseEntity<String> response = delete("/cmdb-crud/existing-item", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void itemNotOwnerCannotDeleteIt() throws Exception {
        when(pdp.canManage("user", "existing-item")).thenReturn(false);

        ResponseEntity<String> response = delete("/cmdb-crud/existing-item", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // Wrong paths
    //

    @Test
    public void badRequestWhenWrongUrl() throws Exception {
        assertThat(post("/cmdb-crud/existing-item/more", "valid", CREATE_REQUEST_BODY).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(put("/cmdb-crud/existing-item/more", "valid", UPDATE_REQUEST_BODY).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(delete("/cmdb-crud/existing-item/more", "valid").getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
