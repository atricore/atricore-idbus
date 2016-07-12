package org.atricore.idbus.capabilities.oauth2.main.emitter;

import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmissionContext;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2SecurityTokenEmissionContext extends AbstractSecurityTokenEmissionContext {

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
