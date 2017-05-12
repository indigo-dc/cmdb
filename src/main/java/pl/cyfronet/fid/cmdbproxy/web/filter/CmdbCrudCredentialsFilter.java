package pl.cyfronet.fid.cmdbproxy.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class CmdbCrudCredentialsFilter extends CmdbCrudAwareFilter {

    @Value("${proxy.cmdb-crud.username}")
    private String username;

    @Value("${proxy.cmdb-crud.password}")
    private String password;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(new CmdbCrudCredentialsHttpRequest(request), response);
    }

    private class CmdbCrudCredentialsHttpRequest extends HttpServletRequestWrapper {

        private static final String BASIC_AUTH_HEADER_TEMPATE = "Basic %s";
        private static final String AUTHORIZATION_HEADER_NAME = "authorization";

        public CmdbCrudCredentialsHttpRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if (isAuthorizationHeader(name)) {
                return getBasicAuth();
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (isAuthorizationHeader(name)) {
                return Collections.enumeration(Arrays.asList(getBasicAuth()));
            }
            return super.getHeaders(name);
        }

        private boolean isAuthorizationHeader(String name) {
            return AUTHORIZATION_HEADER_NAME.equalsIgnoreCase(name);
        }

        private String getBasicAuth() {
            return String.format(BASIC_AUTH_HEADER_TEMPATE,
                    Base64.encodeBase64String((username + ":" + password).getBytes()));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Enumeration<String> e = super.getHeaderNames();
            Set<String> headerNames = new HashSet<>();
            while(e.hasMoreElements()) {
                String name = e.nextElement();
                if(!AUTHORIZATION_HEADER_NAME.equalsIgnoreCase(name)) {
                    headerNames.add(name);
                }
            }

            if (cmdbCrudRequest((HttpServletRequest)getRequest())) {
                headerNames.add(AUTHORIZATION_HEADER_NAME);
            }

            return Collections.enumeration(headerNames);
        }
    }
}
