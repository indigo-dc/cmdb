package pl.cyfronet.fid.cmdbproxy.pdp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Service
@Profile({"development", "production"})
public class CmdbPdp implements Pdp {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Item {
        @JsonProperty("_id")
        String id;

        @JsonProperty("data")
        Map<String, Object> data;

        @JsonProperty("type")
        String type;
    }

    private String targetUrl;

    private EntityStructure entityStructure;

    @Autowired
    public CmdbPdp(EntityStructure entityStructure, @Value("${proxy.cmdb-crud.target_url}") String targetUrl) {
        this.entityStructure = entityStructure;
        this.targetUrl = targetUrl;
    }

    @Override
    public boolean canManage(String userId, String entityId) {
        return canManage(userId, entityId, null);
    }

    private boolean canManage(String userId, String entityId, String expectedType) {
        try {
            Item item = new RestTemplate().getForObject(targetUrl + "/" + entityId, Item.class);
            Entity entity = entityStructure.getEntity(item.type);
            @SuppressWarnings("unchecked")
            List<String> owners = (List<String>)item.data.get("owners");

            if(isWrongType(expectedType, item.type)) {
                return false;
            } else if(notNullable(owners).contains(userId)) {
                return true;
            } else {
                return notNullable(entity.getParents()).stream()
                        .anyMatch(parent -> canManage(userId,
                                                      (String) item.data.get(parent.getForeignKey()),
                                                      parent.type));
            }
        } catch(Exception e) {
            return false;
        }
    }

    private boolean isWrongType(String expected, String given) {
        return expected != null && !expected.equals(given);
    }

    private <T> List<T> notNullable(List<T> list) {
        return Optional.ofNullable(list).orElse(Collections.<T>emptyList());
    }
}
