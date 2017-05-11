package pl.cyfronet.fid.cmdbproxy;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.cyfronet.fid.cmdbproxy.pdp.Pdp;

@Configuration
public class TestConfig {

    @Bean
    public Pdp pdp() {
        return mock(Pdp.class);
    }
}
