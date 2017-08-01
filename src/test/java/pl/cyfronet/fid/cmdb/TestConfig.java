package pl.cyfronet.fid.cmdb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.cyfronet.fid.cmdb.pdp.Entity;
import pl.cyfronet.fid.cmdb.pdp.EntityStructure;
import pl.cyfronet.fid.cmdb.pdp.Pdp;

@Configuration
public class TestConfig {

    @Bean
    public Pdp pdp() {
        return mock(Pdp.class);
    }

    @Bean
    public EntityStructure entityStructure() {
        EntityStructure entityStructure = mock(EntityStructure.class);

        Entity provider = new Entity("provider");
        Entity service = new Entity("service");
        Entity image = new Entity("image");

        provider.getChildren().put("services", service);
        service.getChildren().put("images", image);

        service.getParents().put("provider_id", provider);
        image.getParents().put("service", service);

        when(entityStructure.isRoot("provider")).thenReturn(true);
        when(entityStructure.isRoot("service")).thenReturn(false);
        when(entityStructure.isRoot("image")).thenReturn(false);

        when(entityStructure.getEntity("provider")).thenReturn(provider);
        when(entityStructure.getEntity("service")).thenReturn(service);
        when(entityStructure.getEntity("image")).thenReturn(image);

        return entityStructure;
    }
}
