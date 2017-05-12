package pl.cyfronet.fid.cmdbproxy.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class CmdbCrudAwareFilter extends OncePerRequestFilter {

    @Value("${proxy.cmdb-crud.servlet_url}")
    private String cmdbCrudUrl;

    protected boolean cmdbCrudRequest(HttpServletRequest request) {
        return cmdbCrudUrl.equals(request.getServletPath() + "/*");
    }
}
