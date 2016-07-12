package org.atricore.idbus.capabilities.atricoreid.as.main.emitter;

import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AtricoreIDAccessTokenType;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmissionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDSecurityTokenEmissionContext extends AbstractSecurityTokenEmissionContext {

    private AtricoreIDAccessTokenType accessToken;

    private CircleOfTrustMemberDescriptor member;

    private String identityPlanName;

    public AtricoreIDSecurityTokenEmissionContext() {
    }
/*
    public SamlR2SecurityTokenEmissionContext(AuthenticationState authnState,
                                              CircleOfTrustMemberDescriptor member,
                                              MetadataEntry roleMetadata) {

        this.member = member;
        this.roleMetadata = roleMetadata;
        this.authnState = authnState;
    }

    public RequestAbstractType getRequest() {
        return authnState.getAuthnRequest();
    }
    */

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

    public void setAccessToken(AtricoreIDAccessTokenType accessToken) {
        this.accessToken = accessToken;
    }

    public AtricoreIDAccessTokenType getAccessToken() {
        return accessToken;
    }
}
