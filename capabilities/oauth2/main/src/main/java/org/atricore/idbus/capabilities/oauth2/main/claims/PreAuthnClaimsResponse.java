package org.atricore.idbus.capabilities.oauth2.main.claims;

/**
 * Created by sgonzalez on 10/22/14.
 */
public class PreAuthnClaimsResponse {

    private String preAuthnToken;

    private String inReplyTo;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getPreAuthnToken() {
        return preAuthnToken;
    }

    public void setPreAuthnToken(String preAuthnToken) {
        this.preAuthnToken = preAuthnToken;
    }
}
