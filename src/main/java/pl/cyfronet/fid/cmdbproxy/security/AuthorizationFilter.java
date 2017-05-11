package pl.cyfronet.fid.cmdbproxy.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import pl.cyfronet.fid.cmdbproxy.pdp.Pdp;

@Component
public class AuthorizationFilter implements Filter {

    @Value("${proxy.cmdb-crud.servlet_url}")
    private String cmdbCrudUrl;

    @Autowired
    private Pdp pdp;

     private RequestMappingHandlerMapping mapping;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
         mapping = new RequestMappingHandlerMapping();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isMethod(httpRequest, HttpMethod.GET)) {
            chain.doFilter(request, response);
        } else if (cmdbCrudRequest(httpRequest)) {
            ResettableStreamHttpServletRequest resettableRequest = new ResettableStreamHttpServletRequest(httpRequest);

            if (isCreate(resettableRequest)) {
                doCreateFilter(resettableRequest, httpResponse, chain);
            } else if(isMethod(resettableRequest, HttpMethod.PUT, HttpMethod.DELETE)) {
                doManageFilter(resettableRequest, httpResponse, chain);
            } else {
                permissionDenied((HttpServletResponse) response);
            }
        } else {
            permissionDenied((HttpServletResponse) response);
        }
    }

    private void doCreateFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (mapping.match(request, "/") == null && mapping.match(request, "/{id}") == null) {
            badRequest(response);
        } else {
            if (canCreate(request)) {
                chain.doFilter(request, response);
            } else {
                permissionDenied(response);
            }
        }
    }

    private void doManageFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String itemId = getItemId(request);
        if (itemId != null) {
            if (canManage(itemId)) {
                chain.doFilter(request, response);
            } else {
                permissionDenied(response);
            }
        } else {
            badRequest(response);
        }
    }

    private String getItemId(HttpServletRequest request) {
        RequestMatchResult match = mapping.match(request, "/{id}");
        if (match != null) {
            Map<String, String> variables = match.extractUriTemplateVariables();
            return variables.get("id");
        } else {
            return null;
        }
    }

    private boolean isCreate(HttpServletRequest httpRequest) {
        return isMethod(httpRequest, HttpMethod.POST, HttpMethod.PUT) && isCreateRequestBody(httpRequest);
    }

    private boolean canCreate(ServletRequest request) {
        try {
            return pdp.canCreate(getCurrentUser(), request.getInputStream());
        } catch (IOException e) {
            return false;
        }
    }

    private boolean canManage(String itemId) {
        return pdp.canManage(getCurrentUser(), itemId);
    }

    private boolean isCreateRequestBody(ServletRequest request) {
        try {
            return !IOUtils.toString(request.getInputStream(), Charset.defaultCharset()).contains("\"_rev\":");
        } catch (IOException e) {
            return false;
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

    private void badRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
    }

    private String getCurrentUser() {
        return (String) ((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
                .getPrincipal();
    }

    @Override
    public void destroy() {

    }

    private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            readRawData();
            return servletStream;
        }

        private void readRawData() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader(), Charset.defaultCharset());
            }
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            readRawData();
            return new BufferedReader(new InputStreamReader(servletStream));
        }

        private class ResettableServletInputStream extends ServletInputStream {

            private InputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        }
    }
}
