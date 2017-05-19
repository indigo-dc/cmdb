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

    private Map<String, Entity> entities;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CmdbEntity {
        @JsonProperty("belongs_to")
        List<Parent> belongsTo;

        @JsonProperty("has_many")
        List<Child> hasMany;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Child {
        @JsonProperty("name")
        String name;

        @JsonProperty("type")
        String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Parent {
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

        entities = entitiesNames.stream()
                .map(en -> new Entity(en)).collect(Collectors.toMap(e -> e.type, e -> e));

        entities.values().forEach(e -> {
            CmdbEntity cmdbEntity = getCmdbEntity(e.type);

            notNullable(cmdbEntity.belongsTo).forEach(p -> e.parents.put(p.foreignKey, entities.get(p.type)));
            notNullable(cmdbEntity.hasMany).forEach(ch -> e.children.put(ch.name, entities.get(ch.type)));
        });
    }

    private CmdbEntity getCmdbEntity(String entityName) {
        return new RestTemplate().getForObject(targetUrl + "/" + entityName + "/schema", CmdbEntity.class);
    }

    private <T> List<T> notNullable(List<T> list) {
        return Optional.ofNullable(list).orElse(Collections.<T>emptyList());
    }

    @Override
    public Entity getEntity(String entityName) {
        return Optional.ofNullable(entities.get(entityName)).orElse(new Entity(entityName));
    }

    @Override
    public boolean isRoot(String entityName) {
        return getEntity(entityName).getParents().size() == 0;
    }
}
