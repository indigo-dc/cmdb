package pl.cyfronet.fid.cmdbproxy.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import pl.cyfronet.fid.cmdbproxy.web.ResettableStreamHttpServletRequest;

@Component
@Order(2)
public class ResettableRequestStreamFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ResettableStreamHttpServletRequest resettableRequest =
                new ResettableStreamHttpServletRequest(request);

        filterChain.doFilter(resettableRequest, response);
    }
}
