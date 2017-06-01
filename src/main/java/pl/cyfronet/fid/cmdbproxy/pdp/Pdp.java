package pl.cyfronet.fid.cmdbproxy.pdp;

import java.io.InputStream;

public interface Pdp {
    boolean canManage(String userId, String entityId);
    boolean canCreate(String userId, InputStream itemPayload);
}
