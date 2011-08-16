package org.atricore.idbus.capabilities.josso.main;

import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JossoAuthnContext implements Serializable {

    // Received back to for SSO (will be ignored!)
    private String ssoBackTo;

    // Request sent to SSO endpoint
    private SPInitiatedAuthnRequestType authnRequest;

    // Received back to for SLO (will be ignored!)
    private String sloBackTo;

    // Request setn to SLO endponit
    private SPInitiatedLogoutRequestType sloRequest;

    // Application ID
    private String appId;

    // Selected IDP Alias
    private String idpAlias;

    // Josso authentication assertio assertion
    private JossoAuthenticationAssertion authnAssertion;

    public String getSsoBackTo() {
        return ssoBackTo;
    }

    public void setSsoBackTo(String ssoBackTo) {
        this.ssoBackTo = ssoBackTo;
    }

    public String getSloBackTo() {
        return sloBackTo;
    }

    public void setSloBackTo(String sloBackTo) {
        this.sloBackTo = sloBackTo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

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

    public void setAuthnAssertion(JossoAuthenticationAssertion aa) {
        this.authnAssertion = aa;
    }

    public JossoAuthenticationAssertion getAuthnAssertion() {
        return this.authnAssertion;
    }

    public void setSloRequest(SPInitiatedLogoutRequestType request) {
        this.sloRequest = request;
    }

    public SPInitiatedLogoutRequestType getSloRequest() {
        return sloRequest;
    }
}
