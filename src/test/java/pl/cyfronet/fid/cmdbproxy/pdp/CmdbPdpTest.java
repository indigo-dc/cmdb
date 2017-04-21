package pl.cyfronet.fid.cmdbproxy.pdp;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(pdp.canManage("userA", "provider", "provider-100IT")).isFalse();
        assertThat(pdp.canManage("userD", "provider", "provider-100IT")).isTrue();
    }

    @Test
    public void testLeafWithRootOwnership() throws Exception {
        // userB is in parent service, userC is in parent provider
        assertThat(pdp.canManage("userB", "image", "7efc59c5db69ea67c5100de0f72580ea")).isTrue();
        assertThat(pdp.canManage("userC", "image", "7efc59c5db69ea67c5100de0f72580ea")).isTrue();
    }

    @Test
    public void testNoOwners() throws Exception {
        // userA is not available in parent objects, userC is in parent provider
        assertThat(pdp.canManage("userA", "service", "noowners")).isFalse();
        assertThat(pdp.canManage("userC", "service", "noowners")).isTrue();
    }
}
