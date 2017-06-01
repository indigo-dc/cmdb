package pl.cyfronet.fid.cmdbproxy.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class CustomHeaderHttpServletRequest extends HttpServletRequestWrapper {

    private Map<String, String> customHeaders = new HashMap<>();

    public CustomHeaderHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void addCustomHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            return Collections.enumeration(Arrays.asList(customHeaders.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Enumeration<String> e = super.getHeaderNames();
        Set<String> headerNames = new HashSet<>();
        while(e.hasMoreElements()) {
            String name = e.nextElement();
            if(!hasCustomHeader(name)) {
                headerNames.add(name);
            }
        }

        headerNames.addAll(customHeaders.keySet());

        return Collections.enumeration(headerNames);
    }

    private boolean hasCustomHeader(String headerName) {
        return customHeaders.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(headerName));
    }
}
