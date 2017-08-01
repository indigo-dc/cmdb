package pl.cyfronet.fid.cmdb.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.cyfronet.fid.cmdb.pdp.EntityStructure;
import pl.cyfronet.fid.cmdb.service.Fetcher;
import pl.cyfronet.fid.cmdb.util.CollectionUtil;

@Component
@Order(6)
public class GuardRestrictedElementsFilter extends CmdbCrudAwareFilter {

    private static final String TYPE = "type";

    @Autowired
    private Fetcher fetcher;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    EntityStructure structure;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isCmdbCrudRequest(request) && isUpdate(request) && isTryingToOverrideProtectedElements(request)) {
            badRequest(response, "Cannot update protected (type) elements");
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isTryingToOverrideProtectedElements(HttpServletRequest request) {
        String id = (String) request.getAttribute(AuthorizationFilter.CMDB_ID);
        Map<String, Object> item = fetcher.getItem(id);
        Map<String, Object> updatedItem = getUpdatedItem(request);
        return !eq(item.get(TYPE), updatedItem.get(TYPE)) || isTryingToOverrideRelation((String) item.get(TYPE),
                CollectionUtil.notNullable((Map<String, Object>) item.get("data")),
                CollectionUtil.notNullable((Map<String, Object>) updatedItem.get("data")));
    }

    private boolean isTryingToOverrideRelation(String entityName, Map<String, Object> oryginal,
            Map<String, Object> updated) {
        return structure.getEntity(entityName).getRestrictedParameters().stream()
                .anyMatch(relationField -> !eq(oryginal.get(relationField), updated.get(relationField)));
    }

    private boolean eq(Object v1, Object v2) {
        return v1 == null && v2 == null || v1.equals(v2);
    }

    private Map<String, Object> getUpdatedItem(HttpServletRequest request) {
        try {
            return mapper.readValue(request.getInputStream(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
