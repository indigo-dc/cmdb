package pl.cyfronet.fid.cmdbproxy;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "pl.cyfronet.fid" })
public class ApplicationConfig {

    @Value("${proxy.cmdb.servlet_url}")
    private String servletUrl;

    @Value("${proxy.cmdb.target_url}")
    private String targetUrl;

    @Value("${proxy.cmdb.logging_enabled ?: false}")
    private String loggingEnabled;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), servletUrl);
        servletRegistrationBean.addInitParameter("targetUri", targetUrl);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, loggingEnabled);

        return servletRegistrationBean;
    }
}
