package pl.cyfronet.fid.cmdbproxy.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.cyfronet.fid.cmdbproxy.pdp.EntityStructure;
import pl.cyfronet.fid.cmdbproxy.web.ResettableStreamHttpServletRequest;

@Component
@Order(4)
public class OwnersValidationFilter extends CmdbCrudAwareFilter {

    private static final String OWNERS_TAG = "owners";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityStructure structure;

    @SuppressWarnings("serial")
    private static class BadRequest extends Exception {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (cmdbCrudRequest(request)) {
            try {
                if (isCreate(request)) {
                    guaranteeOwnerExists(request, response, filterChain);
                } else if (isMethod(request, HttpMethod.PUT)) {
                    checkOwnerExists(request, response, filterChain);
                }
                filterChain.doFilter(request, response);
            } catch (BadRequest e) {
                badRequest(response, "Wrong input data format");
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

    @SuppressWarnings("unchecked")
    private void checkOwnerExists(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws BadRequest {
        Map<String, Object> item = getItem(request);

        if (isRootItem(item)) {
            if (!item.containsKey(OWNERS_TAG) && item.get(OWNERS_TAG) instanceof List) {
                List<String> owners = (List<String>) item.get(OWNERS_TAG);
                if (owners != null && owners.size() > 0) {
                    return;
                }
            }
            throw new BadRequest();
        }
    }

    private void guaranteeOwnerExists(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException, BadRequest {
        Map<String, Object> item = getItem(request);
        if (isRootItem(item)) {
            guaranteeOwnerExists(item, (ResettableStreamHttpServletRequest) request);
        }
    }

    private boolean isRootItem(Map<String, Object> item) {
        return structure.isRoot((String) item.get("type"));
    }

    @SuppressWarnings("unchecked")
    private void guaranteeOwnerExists(Map<String, Object> item, ResettableStreamHttpServletRequest request)
            throws BadRequest {
        try {
            if (!item.containsKey(OWNERS_TAG) || !(item.get(OWNERS_TAG) instanceof List)) {
                item.put(OWNERS_TAG, new ArrayList<String>());
            }
            List<String> owners = (List<String>) item.get(OWNERS_TAG);

            if (owners.isEmpty()) {
                owners.add(getCurrentUser());
                String newBody = mapper.writeValueAsString(item);
                request.resetInputStream(newBody.getBytes());
            }
        } catch (Exception e) {
            throw new BadRequest();
        }
    }

    private Map<String, Object> getItem(HttpServletRequest request) throws BadRequest {
        try {
            return mapper.readValue(request.getInputStream(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new BadRequest();
        }
    }
}
