package org.atricore.idbus.capabilities.oauth2.main.emitter;

import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2SecurityTokenEmissionContext implements Serializable {

    private Subject subject;

    private String sessionIndex;

    private SSOSession ssoSession;

    private String identityPlanName;

    private CircleOfTrustMemberDescriptor member;

    // OAuth2 Specific information
    private OAuthAccessTokenType accessToken;

    public OAuth2SecurityTokenEmissionContext() {
    }

    public CircleOfTrustMemberDescriptor getMember() {
        return member;
    }

    public void setMember(CircleOfTrustMemberDescriptor member) {
        this.member = member;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setSessionIndex(String sessionIndex) {
        this.sessionIndex = sessionIndex;
    }

    public String getSessionIndex() {
        return this.sessionIndex;
    }

    public SSOSession getSsoSession() {
        return ssoSession;
    }

    public void setSsoSession(SSOSession ssoSession) {
        this.ssoSession = ssoSession;
    }

    public void setIdentityPlanName(String identityPlanName) {
        this.identityPlanName = identityPlanName;
    }

    public String getIdentityPlanName() {
        return identityPlanName;
    }

    public void setAccessToken(OAuthAccessTokenType accessToken) {
        this.accessToken = accessToken;
    }

    public OAuthAccessTokenType getAccessToken() {
        return accessToken;
    }
}
