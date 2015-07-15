package org.atricore.idbus.capabilities.sso.main.idp;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;

/**
 * SP Security context reference from IdPs
 */
public class ProviderSecurityContext implements java.io.Serializable {

    private NameIDType providerId;

    private String relayState;

    private int sloStatus;

    private LogoutRequestType sloRequest;

    public ProviderSecurityContext(NameIDType providerId,
                            String relayState) {

        this.providerId = providerId;
        this.relayState = relayState;
        this.sloStatus = IdentityProviderConstants.SP_SLO_NONE;
    }

    public NameIDType getProviderId() {
        return providerId;
    }

    public String getRelayState() {
        return relayState;
    }

    public int getSloStatus() {
        return sloStatus;
    }

    public void setSloStatus(int sloStatus) {
        this.sloStatus = sloStatus;
    }

    public LogoutRequestType getSloRequest() {
        return sloRequest;
    }

    public void setSloRequest(LogoutRequestType sloRequest) {
        this.sloRequest = sloRequest;
    }
}