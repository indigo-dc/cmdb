package pl.cyfronet.fid.cmdbproxy.pdp;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import pl.cyfronet.fid.cmdbproxy.WireMockTest;

public class CmdbPdpTest extends WireMockTest {

    @Value("${proxy.cmdb.target_url}")
    private String targetUrl;
    private CmdbPdp pdp;

    @Before
    public void setUp() {
        EntityStructure structure = new CmdbEntityStructure(targetUrl);
        pdp = new CmdbPdp(structure, targetUrl);
    }

    @Test
    public void testCanManageRoot() throws Exception {
        // userD is in provider, userA does not.
        assertThat(pdp.canManage("userA", "provider-100IT")).isFalse();
        assertThat(pdp.canManage("userD", "provider-100IT")).isTrue();
    }

    @Test
    public void testLeafWithRootOwnership() throws Exception {
        // userB is in parent service, userC is in parent provider
        assertThat(pdp.canManage("userB", "7efc59c5db69ea67c5100de0f72580ea")).isTrue();
        assertThat(pdp.canManage("userC", "7efc59c5db69ea67c5100de0f72580ea")).isTrue();
    }

    @Test
    public void testNoOwners() throws Exception {
        // userA is not available in parent objects, userC is in parent provider
        assertThat(pdp.canManage("userA", "noowners")).isFalse();
        assertThat(pdp.canManage("userC", "noowners")).isTrue();
    }

    @Test
    public void testGuardHierarchicalStructure() throws Exception {
        // userA is available in parent objects but parent has wrong type
        assertThat(pdp.canManage("userA", "wrongParentType")).isFalse();
    }

    @Test
    public void testCanCreateLeaf() throws Exception {
        // userB is in parent service, userC is in parent provider
        String newItem = "{\"type\": \"image\", \"data\": {\"service\": \"4401ac5dc8cfbbb737b0a025758cf045\"}}";

        assertThat(pdp.canCreate("userA", IOUtils.toInputStream(newItem, Charset.defaultCharset()))).isFalse();
        assertThat(pdp.canCreate("userB", IOUtils.toInputStream(newItem, Charset.defaultCharset()))).isTrue();
        assertThat(pdp.canCreate("userC", IOUtils.toInputStream(newItem, Charset.defaultCharset()))).isTrue();
    }

    @Test
    public void testEveryoneCanCreateRoot() throws Exception {
        String rootItem = "{\"type\": \"provider\"}";

        assertThat(pdp.canCreate("user", IOUtils.toInputStream(rootItem, Charset.defaultCharset()))).isTrue();
    }
}
