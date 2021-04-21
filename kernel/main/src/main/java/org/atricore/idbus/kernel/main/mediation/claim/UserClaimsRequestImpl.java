package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
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
    private Locale locale;

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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getRelayState() {
        return relayState;
    }

    @Override
    public Channel getIssuerChannel() {
        return issuerChannel;
    }

    @Override
    public IdentityMediationEndpoint getIssuerEndpoint() {
        return issuerEndpoint;
    }

    @Override
    public String getLastErrorId() {
        return lastErrorId;
    }

    @Override
    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    public void setLastErrorId(String lastErrorId) {
        this.lastErrorId = lastErrorId;
    }

    public void setLastErrorMsg(String lastErrorMsg) {
        this.lastErrorMsg = lastErrorMsg;
    }

    @Override
    public Object getAttribute(String key) {
        return attrs.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attrs.put(key, (Serializable) value);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
