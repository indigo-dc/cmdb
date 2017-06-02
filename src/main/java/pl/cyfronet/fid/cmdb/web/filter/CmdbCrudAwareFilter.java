package pl.cyfronet.fid.cmdb.web.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class CmdbCrudAwareFilter extends OncePerRequestFilter {

	@Value("${proxy.cmdb-crud.servlet_url}")
	private String cmdbCrudUrl;

	protected boolean isCmdbCrudRequest(HttpServletRequest request) {
		return cmdbCrudUrl.equals(request.getServletPath() + "/*");
	}

	protected boolean isCreate(HttpServletRequest httpRequest) {
		return isMethod(httpRequest, HttpMethod.POST, HttpMethod.PUT) && isCreateRequestBody(httpRequest);
	}

	protected boolean isUpdate(HttpServletRequest httpRequest) {
        return isMethod(httpRequest, HttpMethod.PUT) && !isCreateRequestBody(httpRequest);
    }

	protected boolean isMethod(HttpServletRequest request, HttpMethod... methods) {
		return Arrays.asList(methods).stream().anyMatch(m -> m.toString().equals(request.getMethod()));
	}

	private boolean isCreateRequestBody(ServletRequest request) {
		try {
			return !IOUtils.toString(request.getInputStream(), Charset.defaultCharset()).contains("\"_rev\":");
		} catch (IOException e) {
			return false;
		}
	}

	protected void badRequest(HttpServletResponse response, String msg) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
	}

	protected String getCurrentUser() {
		return (String) ((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
		        .getPrincipal();
	}
}
