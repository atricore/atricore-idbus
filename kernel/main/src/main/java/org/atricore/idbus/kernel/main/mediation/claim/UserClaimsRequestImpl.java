package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserClaimsRequestImpl implements UserClaimsRequest {

    private String id;
    private String relayState;
    private Channel issuerChannel;
    private IdentityMediationEndpoint issuerEndpoint;
    private String lastErrorId;
    private String lastErrorMsg;
    private Map<String, java.io.Serializable> attrs = new HashMap<String, Serializable>();

    public UserClaimsRequestImpl(String id, Channel issuerChannel, IdentityMediationEndpoint issuerEndpoint) {
        this.id = id;
        this.issuerChannel = issuerChannel;
        this.issuerEndpoint = issuerEndpoint;
    }

    public UserClaimsRequestImpl(String id, Channel issuerChannel, IdentityMediationEndpoint issuerEndpoint, String relayState) {
        this.id = id;
        this.issuerChannel = issuerChannel;
        this.issuerEndpoint = issuerEndpoint;
        this.relayState = relayState;
    }

    public String getId() {
        return id;
    }

    public String getRelayState() {
        return relayState;
    }

    public Channel getIssuerChannel() {
        return issuerChannel;
    }

    public IdentityMediationEndpoint getIssuerEndpoint() {
        return issuerEndpoint;
    }

    public String getLastErrorId() {
        return lastErrorId;
    }

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    public void setLastErrorId(String lastErrorId) {
        this.lastErrorId = lastErrorId;
    }

    public void setLastErrorMsg(String lastErrorMsg) {
        this.lastErrorMsg = lastErrorMsg;
    }

    public Object getAttribute(String key) {
        return attrs.get(key);
    }

    public void setAttribute(String key, Object value) {
        attrs.put(key, (Serializable) value);
    }


}
