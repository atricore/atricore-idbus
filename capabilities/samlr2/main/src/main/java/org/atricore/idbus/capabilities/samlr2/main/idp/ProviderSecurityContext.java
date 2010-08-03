package org.atricore.idbus.capabilities.samlr2.main.idp;

import oasis.names.tc.saml._2_0.assertion.NameIDType;

public class ProviderSecurityContext implements java.io.Serializable {

    private NameIDType providerId;

    private String relayState;

    public ProviderSecurityContext(NameIDType providerId,
                            String relayState) {

        this.providerId = providerId;
        this.relayState = relayState;
    }

    public NameIDType getProviderId() {
        return providerId;
    }

    public String getRelayState() {
        return relayState;
    }

}