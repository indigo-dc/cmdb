package pl.cyfronet.fid.cmdbproxy.pdp;

import java.util.List;

class Entity {
    String type;
    String foreignKey;
    List<Entity> parents;

    public String getType() {
        return type;
    }
    public String getForeignKey() {
        return foreignKey;
    }
    public List<Entity> getParents() {
        return parents;
    }
}
