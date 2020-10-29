package pl.cyfronet.fid.cmdb.requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pl.cyfronet.fid.cmdb.WireMockTest;
import pl.cyfronet.fid.cmdb.pdp.Pdp;

public class AuthorizationFilterTest extends WireMockTest {

    private static final String CREATE_REQUEST_BODY =
            "{\"type\": \"image\", \"data\": {\"service\": \"4401ac5dc8cfbbb737b0a025758cf045\"}}";

    private static final String CREATE_ROOT_ITEM_REQUEST_BODY =
            "{\"type\": \"provider\", \"data\": {\"name\": \"root item\"}}";

    private static final String UPDATE_REQUEST_BODY =
            "{\"_rev\": \"rev\", \"type\": \"image\", \"owners\": [\"user\"], \"data\": {\"service\": \"service_id\"}}";

    @Autowired
    private Pdp pdp;

    @Test
    public void getAllowedForAllRequests() throws Exception {
        stubOKUserInfo("valid", "user");
        stubGetOk("/get", "ok");
        stubGetOk("/crud/get", "ok");

        assertThat(get("/cmdb/_design/schema/_rewrite/get", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get("/cmdb-crud/get", "valid").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void otherThanGetAllowedOnlyForCMDBCrud() throws Exception {
        stubOKUserInfo("valid", "user");
        stubPutOk("/crud/leaf", "{\"_rev\": \"rev\"}");
        when(pdp.canManage("user", "leaf")).thenReturn(true);

        assertThat(put("/cmdb/leaf", "valid", UPDATE_REQUEST_BODY).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(put("/cmdb-crud/leaf", "valid", UPDATE_REQUEST_BODY).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    //
    // POST - Create
    //

    @Test
    public void parentOwnerCanCreateChild() throws Exception {
        stubOKUserInfo("valid", "user");
        stubPostOk("/crud", "ok");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);

        ResponseEntity<String> response = post("/cmdb-crud", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void nonParentOwnerCannotCreateChild() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(false);

        ResponseEntity<String> response = post("/cmdb-crud", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void adminCanDoEverything() throws Exception {
        stubOKAdminInfo("valid", "admin");
        stubPostOk("/crud", "ok");

        ResponseEntity<String> response = post("/cmdb-crud", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
	public void onlyAdminCanCreateRootItem() throws Exception {
        stubOKUserInfo("validUser", "user");
        stubOKAdminInfo("validAdmin", "admin");
        stubPostOk("/crud", "ok");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(false);
        when(pdp.canCreate(eq("admin"), any(InputStream.class))).thenReturn(false);

        ResponseEntity<String> userResponse = post("/cmdb-crud", "validUser", CREATE_ROOT_ITEM_REQUEST_BODY);
        ResponseEntity<String> adminResponse = post("/cmdb-crud", "validAdmin", CREATE_ROOT_ITEM_REQUEST_BODY);

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(adminResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(adminResponse.getBody()).isEqualTo("ok");
	}

    //
    // PUT - Create with defined id
    //

    @Test
    public void parentOwnerCanCreateChildWithNamedId() throws Exception {
        stubOKUserInfo("valid", "user");
        stubPutOk("/crud/custom-id", "ok");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/custom-id", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void nonParentOwnerCannotCreateChildWithNamedId() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canCreate(eq("user"), any(InputStream.class))).thenReturn(false);

        ResponseEntity<String> response = put("/cmdb-crud/custom-id", "valid", CREATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // PUT - Update existing item
    //

    @Test
    public void itemOwnerCanModifyIt() throws Exception {
        stubOKUserInfo("valid", "user");
        stubPutOk("/crud/leaf", "ok");
        when(pdp.canManage("user", "leaf")).thenReturn(true);

        ResponseEntity<String> response = put("/cmdb-crud/leaf", "valid", UPDATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("ok");
    }

    @Test
    public void itemNotOwnerCannotModifyIt() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canManage("user", "existing-item")).thenReturn(false);

        ResponseEntity<String> response = put("/cmdb-crud/existing-item", "valid", UPDATE_REQUEST_BODY);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // DELETE - Remove existing item
    //

    @Test
    public void itemOwnerCanDeleteIt() throws Exception {
        stubOKUserInfo("valid", "user");
        stubPostOk("/crud/_bulk_docs", "{\"docs\":[{\"_id\":\"existing-item\",\"_deleted\":true,\"_rev\":\"existing-item-rev\"}]}");
        when(pdp.canManage("user", "existing-item")).thenReturn(true);

        ResponseEntity<String> response = delete("/cmdb-crud/existing-item", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void itemNotOwnerCannotDeleteIt() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canManage("user", "existing-item")).thenReturn(false);

        ResponseEntity<String> response = delete("/cmdb-crud/existing-item", "valid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //
    // Wrong paths
    //

    @Test
    public void badRequestWhenWrongUrl() throws Exception {
        stubOKUserInfo("valid", "user");

        assertThat(post("/cmdb-crud/existing-item/more", "valid", CREATE_REQUEST_BODY).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(put("/cmdb-crud/existing-item/more", "valid", UPDATE_REQUEST_BODY).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(delete("/cmdb-crud/existing-item/more", "valid").getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
