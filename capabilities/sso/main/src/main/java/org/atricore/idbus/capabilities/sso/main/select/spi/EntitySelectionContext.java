package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EntitySelectionContext {

    private UserClaimSet attributes;

    private Map<String, UserClaim> attrsIdx = new HashMap<String, UserClaim>();

    private SelectEntityRequestType request;

    private CircleOfTrustManager cotManager;

    public EntitySelectionContext(CircleOfTrustManager cotManager, UserClaimSet attributes, SelectEntityRequestType request) {
        this.attributes = attributes;
        this.request = request;
        this.cotManager = cotManager;
        if (attributes != null) {
            for (UserClaim attr : attributes.getAttributes()) {
                attrsIdx.put(attr.getName(), attr);
            }
        }
    }

    public Object getAttribute(String name) {
        return attrsIdx.get(name);
    }

    public Collection<String> getAttributeNames() {
        return attrsIdx.keySet();
    }

    public SelectEntityRequestType getRequest() {
        return request;
    }

    public CircleOfTrustManager getCotManager() {
        return cotManager;
    }
}
