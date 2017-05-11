package pl.cyfronet.fid.cmdbproxy.pdp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class CmdbEntityStructureTest {

    @Value("${proxy.cmdb.target_url}")
    private String targetUrl;
    private EntityStructure entityStructure;

    @Before
    public void setUp() {
        entityStructure = new CmdbEntityStructure(targetUrl);
    }

    @Test
    public void testRootStructure() throws Exception {
        Entity provider = entityStructure.getEntity("provider");

        assertThat(provider.getType()).isEqualTo("provider");
        assertThat(provider.getForeignKey()).isNull();
        assertThat(provider.getParents()).isEmpty();
    }

    @Test
    public void testLeafStructure() throws Exception {
        Entity image = entityStructure.getEntity("image");

        assertThat(image.getType()).isEqualTo("image");
        assertThat(image.getForeignKey()).isNull();
        assertThat(image.getParents()).isNotEmpty();

        Entity service = image.getParents().get(0);
        assertThat(service.getType()).isEqualTo("service");
        assertThat(service.getForeignKey()).isEqualTo("service");
        assertThat(service.getParents()).isNotEmpty();

        Entity provider = image.getParents().get(0).getParents().get(0);
        assertThat(provider.getType()).isEqualTo("provider");
        assertThat(provider.getForeignKey()).isEqualTo("provider_id");
        assertThat(provider.getParents()).isEmpty();
    }
}
