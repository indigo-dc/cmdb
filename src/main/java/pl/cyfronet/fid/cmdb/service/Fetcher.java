package pl.cyfronet.fid.cmdb.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import pl.cyfronet.fid.cmdb.pdp.Entity;
import pl.cyfronet.fid.cmdb.pdp.EntityStructure;
import pl.cyfronet.fid.cmdb.util.CollectionUtil;

@Service
public class Fetcher {

    @Value("${proxy.cmdb-crud.target_url}")
    private String crudUrl;

    @Value("${proxy.cmdb.target_url}")
    private String schemaUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    EntityStructure entityStructure;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class HasMany {
        @JsonProperty("rows")
        List<Id> rows;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Id {
        @JsonProperty("id")
        String id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MultiResponse {
        @JsonProperty("rows")
        List<Rev> rows;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Rev {
        @JsonProperty("id")
        String id;

        @JsonProperty("value")
        Map<String, String> value;

        public String getRev() {
            return value.get("rev");
        }
    }

    private static class Ids {
        @JsonProperty("keys")
        final Collection<String> ids;

        public Ids(Collection<String> ids) {
            this.ids = ids;
        }
    }


    public Set<String> getItemAndDependentItems(String id) {
        Map<String, Object> item = getItem(id);

        Entity entity = entityStructure.getEntity((String)item.get("type"));
        Set<String> ids = getChildrenIds(id, entity);
        ids.add(id);

        return ids;
    }

    private Set<String> getChildrenIds(String id, Entity entity) {
        return CollectionUtil.notNullable(entity.getChildren()).entrySet().stream().flatMap(entry -> {
            Set<String> ids = getIds(entity, id, entry.getKey(), entry.getValue());
            Set<String> allIds = new HashSet<>(ids);

            ids.forEach(parentId -> {
                allIds.addAll(getChildrenIds(parentId, entry.getValue()));
            });

            return allIds.stream();
        }).collect(Collectors.toSet());
    }

    private Set<String> getIds(Entity entity, String id, String hasManyReference, Entity childType) {
        HasMany hasMany = restTemplate.getForObject(getHasManyUrl(entity, id, hasManyReference), HasMany.class);

        return hasMany.rows.stream().map(r -> r.id).collect(Collectors.toSet());
    }

    private String getHasManyUrl(Entity entity, String id, String hasManyReference) {
        return String.format("%s/%s/id/%s/has_many/%s", schemaUrl, entity.getType(), id, hasManyReference);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getItem(String id) {
        return restTemplate.getForObject(crudUrl + "/" + id, Map.class);
    }

    public Map<String, String> getRevs(Collection<String> ids) {
        MultiResponse multiResponse = restTemplate.postForObject(crudUrl + "/_all_docs", new Ids(ids), MultiResponse.class);

        return multiResponse.rows.stream().collect(Collectors.toMap(r -> r.id, Rev::getRev));
    }
}
