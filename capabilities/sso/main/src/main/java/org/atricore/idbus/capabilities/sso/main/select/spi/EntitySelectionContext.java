package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.mediation.claim.Claim;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EntitySelectionContext {

    private ClaimSet attributes;

    private Map<String, UserClaim> attrsIdx = new HashMap<String, UserClaim>();

    private SelectEntityRequestType request;

    private CircleOfTrustManager cotManager;

    public EntitySelectionContext(CircleOfTrustManager cotManager, ClaimSet attributes, SelectEntityRequestType request) {
        this.attributes = attributes;
        this.request = request;
        this.cotManager = cotManager;
        if (attributes != null) {
            for (Claim attr : attributes.getClaims()) {
                UserClaim userAttr = (UserClaim) attr;
                attrsIdx.put(userAttr.getName(), userAttr);
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
