package pl.cyfronet.fid.cmdb;

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
    private String cmdbUrl;

    @Value("${proxy.cmdb.target_url}")
    private String cmdbTargetUrl;

    @Value("${proxy.cmdb-crud.servlet_url}")
    private String cmdbCrudUrl;

    @Value("${proxy.cmdb-crud.target_url}")
    private String cmdbCrudTargetUrl;

    @Value("${proxy.cmdb.logging_enabled ?: false}")
    private String loggingEnabled;

    @Bean
    public ServletRegistrationBean cmdbProxyBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), cmdbUrl);
        servletRegistrationBean.addInitParameter("targetUri", cmdbTargetUrl);
        servletRegistrationBean.setName("cmdb");
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, loggingEnabled);

        return servletRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean cmdbCrudProxyBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), cmdbCrudUrl);
        servletRegistrationBean.addInitParameter("targetUri", cmdbCrudTargetUrl);
        servletRegistrationBean.setName("cmdb-crud");
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, loggingEnabled);

        return servletRegistrationBean;
    }
}
