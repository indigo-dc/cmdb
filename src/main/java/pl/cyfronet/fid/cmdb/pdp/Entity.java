package pl.cyfronet.fid.cmdb.pdp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Entity {
    final String type;
    final Map<String, Entity> parents = new HashMap<>();
    final Map<String, Entity> children = new HashMap<>();

    public Entity(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Map<String, Entity> getParents() {
        return parents;
    }

    public Map<String, Entity> getChildren() {
        return children;
    }

    public Collection<String> getRestrictedParameters() {
        return getParents().keySet();
    }
}
