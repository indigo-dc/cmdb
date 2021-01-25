package pl.cyfronet.fid.cmdb.pdp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static pl.cyfronet.fid.cmdb.util.CollectionUtil.notNullable;

@Service
@Profile({"development", "production"})
public class CmdbPdp implements Pdp {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Item {
        @JsonProperty("_id")
        String id;

        @JsonProperty("data")
        Map<String, Object> data;

        @JsonProperty("owners")
        List<String> owners;

        @JsonProperty("type")
        String type;
    }

    private String targetUrl;

    private EntityStructure entityStructure;

    private ObjectMapper mapper;

    private RestTemplate restTemplate;

    @Autowired
    public CmdbPdp(EntityStructure entityStructure, ObjectMapper mapper,
            @Value("${proxy.cmdb-crud.target_url}") String targetUrl, RestTemplate restTemplate) {
        this.entityStructure = entityStructure;
        this.targetUrl = targetUrl;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean canManage(String userId, String entityId) {
        return canManage(userId, entityId, null);
    }

    @Override
    public boolean canCreate(String userId, InputStream itemPayload) {
        try {
            Item item = mapper.readValue(itemPayload, new TypeReference<Item>() {});
            Entity entity = entityStructure.getEntity(item.type);

            return isParentOwner(userId, entity, item);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean canManage(String userId, String entityId, String expectedType) {
        try {
            Item item = restTemplate.getForObject(targetUrl + "/" + entityId, Item.class);
            Entity entity = entityStructure.getEntity(item.type);

            if(isWrongType(expectedType, item.type)) {
                return false;
            } else if(notNullable(item.owners).contains(userId)) {
                return true;
            } else {
                return isParentOwner(userId, entity, item);
            }
        } catch(Exception e) {
            return false;
        }
    }

    private boolean isParentOwner(String userId, Entity entity, Item item) {
        return notNullable(entity.getParents()).entrySet().stream()
                .anyMatch(entry -> canManage(userId,
                                             (String) item.data.get(entry.getKey()),
                                             entry.getValue().type));
    }

    private boolean isWrongType(String expected, String given) {
        return expected != null && !expected.equals(given);
    }
}
