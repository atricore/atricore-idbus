package org.atricore.idbus.capabilities.oauth2.main;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessTokenEnvelope;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;

import java.io.Serializable;

/**
 * Authentication context used
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AuthnContext implements Serializable {

    // Request sent to SSO endpoint
    private SPInitiatedAuthnRequestType authnRequest;

    // Request setn to SLO endponit
    private SPInitiatedLogoutRequestType sloRequest;

    // Selected IDP Alias
    private String idpAlias;

    // Josso authentication assertio assertion
    private OAuth2AccessTokenEnvelope tokenEnvelope;

    public String getIdpAlias() {
        return idpAlias;
    }

    public void setIdpAlias(String idpAlias) {
        this.idpAlias = idpAlias;
    }

    public SPInitiatedAuthnRequestType getAuthnRequest() {
        return authnRequest;
    }

    public void setAuthnRequest(SPInitiatedAuthnRequestType authnRequest) {
        this.authnRequest = authnRequest;
    }

    public void setSloRequest(SPInitiatedLogoutRequestType request) {
        this.sloRequest = request;
    }

    public SPInitiatedLogoutRequestType getSloRequest() {
        return sloRequest;
    }

    public OAuth2AccessTokenEnvelope getTokenEnvelope() {
        return tokenEnvelope;
    }

    public void setTokenEnvelope(OAuth2AccessTokenEnvelope tokenEnvelope) {
        this.tokenEnvelope = tokenEnvelope;
    }
}
