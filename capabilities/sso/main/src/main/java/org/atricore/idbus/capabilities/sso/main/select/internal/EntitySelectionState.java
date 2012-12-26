package org.atricore.idbus.capabilities.sso.main.select.internal;

import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;
import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;

/**
 *
 */
public class EntitySelectionState implements java.io.Serializable {

    private ClaimSet userClaims;

    private SelectEntityRequestType request;

    private Integer userClaimsEndpointIdx;

    public ClaimSet getUserClaims() {
        return userClaims;
    }

    public void setUserClaims(ClaimSet userClaims) {
        this.userClaims = userClaims;
    }

    public SelectEntityRequestType getRequest() {
        return request;
    }

    public void setRequest(SelectEntityRequestType request) {
        this.request = request;
    }

    public Integer getUserClaimsEndpointIdx() {
        return userClaimsEndpointIdx;
    }

    public void setUserClaimsEndpointIdx(Integer userClaimsEndpointIdx) {
        this.userClaimsEndpointIdx = userClaimsEndpointIdx;
    }

}
