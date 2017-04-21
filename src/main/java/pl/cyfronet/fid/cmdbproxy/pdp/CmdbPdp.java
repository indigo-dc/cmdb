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
    }

    private String targetUrl;

    private EntityStructure entityStructure;

    @Autowired
    public CmdbPdp(EntityStructure entityStructure, @Value("${proxy.cmdb.target_url}") String targetUrl) {
        this.entityStructure = entityStructure;
        this.targetUrl = targetUrl;
    }

    @Override
    public boolean canManage(String userId, String entityName, String entityId) {
        return canManage(userId, entityStructure.getEntity(entityName), entityId);
    }

    private boolean canManage(String userId, Entity entity, String entityId) {
        try {
            Item item = new RestTemplate().getForObject(targetUrl + "/" + entity.getType() + "/id/" + entityId, Item.class);
            @SuppressWarnings("unchecked")
            List<String> owners = (List<String>)item.data.get("owners");

            if(Optional.ofNullable(owners).orElse(Collections.<String>emptyList()).contains(userId)) {
                return true;
            } else {
                return Optional.ofNullable(entity.getParents()).orElse(Collections.<Entity>emptyList())
                    .stream().anyMatch(parent -> canManage(userId, parent, (String)item.data.get(parent.getForeignKey())));
            }
        } catch(Exception e) {
            return false;
        }
    }
}
