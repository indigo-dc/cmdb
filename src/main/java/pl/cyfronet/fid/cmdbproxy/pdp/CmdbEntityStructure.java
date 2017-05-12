package pl.cyfronet.fid.cmdbproxy.pdp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Service
@Profile({"development", "production"})
public class CmdbEntityStructure implements EntityStructure {

    private String targetUrl;

    private Map<String, List<BelongsTo>> dependencies;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CmdbEntity {
        @JsonProperty("belongs_to")
        List<BelongsTo> belongsTo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BelongsTo {
        @JsonProperty("type")
        String type;

        @JsonProperty("foreign_key")
        String foreignKey;
    }

    @Autowired
    public CmdbEntityStructure(@Value("${proxy.cmdb.target_url}") String targetUrl) {
        this.targetUrl = targetUrl;
        discoverDependencies();
    }

    private void discoverDependencies() {
        @SuppressWarnings("unchecked")
        List<String> entitiesNames = new RestTemplate().getForObject(targetUrl, List.class);

        dependencies = entitiesNames.stream()
                .collect(Collectors.toMap(en -> en, en -> getDependencies(getCmdbEntity(en))));
    }

    private CmdbEntity getCmdbEntity(String entityName) {
        return new RestTemplate().getForObject(targetUrl + "/" + entityName + "/schema", CmdbEntity.class);
    }

    private List<BelongsTo> getDependencies(CmdbEntity entity) {
        return Optional.ofNullable(entity.belongsTo).orElse(Collections.<BelongsTo>emptyList());
    }

    @Override
    public Entity getEntity(String entityName) {
        Entity entity = new Entity();
        entity.type = entityName;

        return populateWithParents(entity);
    }

    private Entity populateWithParents(Entity e) {
        Optional.ofNullable(dependencies.get(e.type)).ifPresent(cmdbParents -> {
            e.parents = getParents(cmdbParents);
        });
        return e;
    }

    private List<Entity> getParents(List<BelongsTo> cmdbParents) {
        return cmdbParents.stream().map(cmdbE -> {
            Entity parent = new Entity();
            parent.type = cmdbE.type;
            parent.foreignKey = cmdbE.foreignKey;

            return populateWithParents(parent);
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isRoot(String entityName) {
        return getEntity(entityName).getParents().size() == 0;
    }
}
