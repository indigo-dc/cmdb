package pl.cyfronet.fid.cmdbproxy.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.cyfronet.fid.cmdbproxy.service.Fetcher;
import pl.cyfronet.fid.cmdbproxy.web.CustomHeaderHttpServletRequest;
import pl.cyfronet.fid.cmdbproxy.web.ResettableStreamHttpServletRequest;

@Component
@Order(5)
public class CascadeDeleteFilter extends CmdbCrudAwareFilter {

    private static class Record {
        @JsonProperty("_id")
        final String id;

        @JsonProperty("_deleted")
        final Boolean deleted = true;

        @JsonProperty("_rev")
        final String rev;

        Record(String id, String rev) {
            this.id = id;
            this.rev = rev;
        }
    }

    private static class BulkUpdate extends CustomHeaderHttpServletRequest {
        public BulkUpdate(HttpServletRequest request, String id) {
            super(request);
            addCustomHeader("Content-Type", "application/json");
        }

        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public String getPathInfo() {
            return "/_bulk_docs";
        }
    }

    @Autowired
    private Fetcher fetcher;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isCmdbCrudRequest(request) && isMethod(request, HttpMethod.DELETE)
                && request.getAttribute(AuthorizationFilter.CMDB_ID) != null) {
            String id = (String) request.getAttribute(AuthorizationFilter.CMDB_ID);

            ((ResettableStreamHttpServletRequest) request)
                    .resetInputStream(mapper.writeValueAsBytes(getDeleteRequest(id)));

            filterChain.doFilter(new BulkUpdate(request, id), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private Map<String, Object> getDeleteRequest(String id) {
        Map<String, Object> request = new HashMap<>();
        request.put("docs", getRecords(id));

        return request;
    }

    private List<Record> getRecords(String id) {
        return fetcher.getRevs(fetcher.getItemAndDependentItems(id)).entrySet().stream()
                .map(i -> new Record(i.getKey(), i.getValue())).collect(Collectors.toList());
    }
}
