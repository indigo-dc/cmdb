package pl.cyfronet.fid.cmdbproxy.pdp;

public interface Pdp {
    boolean canManage(String userId, String entityName, String entityId);
}
