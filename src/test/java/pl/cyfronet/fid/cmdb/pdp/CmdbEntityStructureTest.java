package pl.cyfronet.fid.cmdb.pdp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;

import pl.cyfronet.fid.cmdb.pdp.CmdbEntityStructure;
import pl.cyfronet.fid.cmdb.pdp.Entity;
import pl.cyfronet.fid.cmdb.pdp.EntityStructure;

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
        assertThat(provider.getParents()).isEmpty();
    }

    @Test
    public void testLeafStructure() throws Exception {
        Entity image = entityStructure.getEntity("image");

        assertThat(image.getType()).isEqualTo("image");
        assertThat(image.getParents()).isNotEmpty();

        Entity service = image.getParents().get("service");
        assertThat(service.getType()).isEqualTo("service");
        assertThat(service.getParents()).isNotEmpty();

        Entity provider = image.getParents().get("service").getParents().get("provider_id");
        assertThat(provider.getType()).isEqualTo("provider");
        assertThat(provider.getParents()).isEmpty();
    }

    @Test
    public void testRoot() throws Exception {
        assertTrue(entityStructure.isRoot("provider"));
        assertFalse(entityStructure.isRoot("service"));
        assertFalse(entityStructure.isRoot("image"));
    }

    @Test
    public void testRootChildren() throws Exception {
       Map<String, Entity> children = entityStructure.getEntity("provider").getChildren();

       assertThat(children.size()).isEqualTo(1);
       assertThat(children.get("services").getType()).isEqualTo("service");


       assertThat(children.get("services").getChildren().size()).isEqualTo(1);
       assertThat(children.get("services").getChildren().get("images").getType()).isEqualTo("image");
    }

    @Test
    public void testLeafChildren() throws Exception {
       Map<String, Entity> children = entityStructure.getEntity("image").getChildren();

       assertThat(children.size()).isEqualTo(0);
    }

    @Test
    public void testGetRestrictedParameters() throws Exception {
       assertThat(entityStructure.getEntity("provider").getRestrictedParameters()).isEmpty();
       assertThat(entityStructure.getEntity("not-existing").getRestrictedParameters()).isEmpty();

       assertThat(entityStructure.getEntity("service").getRestrictedParameters().size()).isEqualTo(1);
       assertThat(entityStructure.getEntity("service").getRestrictedParameters()).contains("provider_id");

       assertThat(entityStructure.getEntity("image").getRestrictedParameters().size()).isEqualTo(1);
       assertThat(entityStructure.getEntity("image").getRestrictedParameters()).contains("service");
    }
}
