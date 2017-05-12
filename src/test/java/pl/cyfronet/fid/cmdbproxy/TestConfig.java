package pl.cyfronet.fid.cmdbproxy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.cyfronet.fid.cmdbproxy.pdp.EntityStructure;
import pl.cyfronet.fid.cmdbproxy.pdp.Pdp;

@Configuration
public class TestConfig {

    @Bean
    public Pdp pdp() {
        return mock(Pdp.class);
    }

    @Bean
    public EntityStructure entityStructure() {
        EntityStructure entityStructure = mock(EntityStructure.class);

        when(entityStructure.isRoot("provider")).thenReturn(true);
        when(entityStructure.isRoot("service")).thenReturn(false);
        when(entityStructure.isRoot("image")).thenReturn(false);

        return entityStructure;
    }
}
