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

    private ClaimSet userClaims;

    private Map<String, UserClaim> userClaimsIdx = new HashMap<String, UserClaim>();

    private SelectEntityRequestType request;

    private CircleOfTrustManager cotManager;

    public EntitySelectionContext(CircleOfTrustManager cotManager, ClaimSet userClaims, SelectEntityRequestType request) {
        this.userClaims = userClaims;
        this.request = request;
        this.cotManager = cotManager;
        if (userClaims != null) {
            for (Claim attr : userClaims.getClaims()) {
                UserClaim userAttr = (UserClaim) attr;
                userClaimsIdx.put(userAttr.getName(), userAttr);
            }
        }
    }

    public UserClaim getUserClaim(String name) {
        return userClaimsIdx.get(name);
    }

    public Collection<String> getUserClaimNames() {
        return userClaimsIdx.keySet();
    }

    public SelectEntityRequestType getRequest() {
        return request;
    }

    public CircleOfTrustManager getCotManager() {
        return cotManager;
    }
}
