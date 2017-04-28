package pl.cyfronet.fid.cmdbproxy.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFilter implements Filter {

    @Value("${proxy.cmdb-crud.servlet_url}")
    private String cmdbCrudUrl;

//    private RequestMappingHandlerMapping mapping;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        mapping = new RequestMappingHandlerMapping();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (!cmdbCrudRequest(httpRequest) && !isMethod(httpRequest, HttpMethod.GET)) {
            permissionDenied((HttpServletResponse) response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean cmdbCrudRequest(HttpServletRequest request) {
        return cmdbCrudUrl.equals(request.getServletPath() + "/*");
    }

    private boolean isMethod(HttpServletRequest request, HttpMethod... methods) {
        return Arrays.asList(methods).stream().anyMatch(m -> m.toString().equals(request.getMethod()));
    }

    private void permissionDenied(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }

    @Override
    public void destroy() {

    }
}
