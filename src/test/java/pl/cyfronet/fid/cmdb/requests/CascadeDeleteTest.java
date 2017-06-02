package pl.cyfronet.fid.cmdb.requests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.tomakehurst.wiremock.client.WireMock;

import pl.cyfronet.fid.cmdb.WireMockTest;
import pl.cyfronet.fid.cmdb.pdp.Pdp;

public class CascadeDeleteTest extends WireMockTest {

    private static final String BULK_DELETE = "{"
            + "\"docs\": ["
            +   "{\"_id\": \"provider-delete\", \"_deleted\":true, \"_rev\": \"provider-delete-rev\"},"
            +   "{\"_id\": \"service-delete1\", \"_deleted\":true, \"_rev\": \"service-delete1-rev\"},"
            +   "{\"_id\": \"service-delete2\", \"_deleted\":true, \"_rev\": \"service-delete2-rev\"},"
            +   "{\"_id\": \"service-delete3\", \"_deleted\":true, \"_rev\": \"service-delete3-rev\"},"
            +   "{\"_id\": \"image-delete1\", \"_deleted\":true, \"_rev\": \"image-delete1-rev\"},"
            +   "{\"_id\": \"image-delete2\", \"_deleted\":true, \"_rev\": \"image-delete2-rev\"}"
            + "]}";

    @Autowired
    private Pdp pdp;

    @Test
    public void cascadeDeleteUsesBulkUpdate() throws Exception {
        stubOKUserInfo("valid", "user");
        when(pdp.canManage("user", "provider-delete")).thenReturn(true);

        stubFor(WireMock.post(urlEqualTo("/crud/_bulk_docs"))
                .withRequestBody(equalToJson(BULK_DELETE, true, false))
                .willReturn(aResponse()
                    .withBody("ok")
                    .withStatus(HttpStatus.OK.value())));

        ResponseEntity<String> response = delete("/cmdb-crud/provider-delete", "valid");

        WireMock.verify(postRequestedFor(urlEqualTo("/crud/_bulk_docs"))
                .withRequestBody(equalToJson(BULK_DELETE, true, false))
                .withHeader("Content-Type", equalTo("application/json")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
