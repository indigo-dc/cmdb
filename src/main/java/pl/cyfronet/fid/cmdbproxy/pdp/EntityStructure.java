package pl.cyfronet.fid.cmdbproxy.pdp;

public interface EntityStructure {
    Entity getEntity(String entityName);
    boolean isRoot(String entityName);
}
