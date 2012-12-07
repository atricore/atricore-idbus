package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;

/**
*/
public class UserClaimsResponseImpl implements UserClaimsResponse {

    private String id;
    private String relayState;
    private Channel issuer;
    private String inResponseTo;
    private UserClaimSet claimSet;

    public UserClaimsResponseImpl(String id, Channel issuer, String inResponseTo, UserClaimSet claimSet, String relayState) {
        this.id = id;
        this.relayState = relayState;
        this.issuer = issuer;
        this.inResponseTo = inResponseTo;
        this.claimSet = claimSet;
    }

    public String getId() {
        return id;
    }

    public String getRelayState() {
        return relayState;
    }

    public Channel getIssuer() {
        return issuer;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public UserClaimSet getAttributeSet() {
        return claimSet;
    }
}
