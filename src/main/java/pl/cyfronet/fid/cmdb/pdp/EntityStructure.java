package pl.cyfronet.fid.cmdb.pdp;

public interface EntityStructure {
    Entity getEntity(String entityName);
    boolean isRoot(String entityName);
}
