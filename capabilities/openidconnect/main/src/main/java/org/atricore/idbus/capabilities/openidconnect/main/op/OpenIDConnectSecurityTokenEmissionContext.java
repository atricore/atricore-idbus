package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * Created by sgonzalez on 8/5/15.
 */
public class OpenIDConnectSecurityTokenEmissionContext implements Serializable {

        private Subject subject;

        private String sessionIndex;

        private SSOSession ssoSession;

        private String identityPlanName;

        private CircleOfTrustMemberDescriptor member;

        private AccessToken accessToken;

        private RefreshToken refreshToken;

        private String idToken;

        public Subject getSubject() {
                return subject;
        }

        public void setSubject(Subject subject) {
                this.subject = subject;
        }

        public String getSessionIndex() {
                return sessionIndex;
        }

        public void setSessionIndex(String sessionIndex) {
                this.sessionIndex = sessionIndex;
        }

        public SSOSession getSsoSession() {
                return ssoSession;
        }

        public void setSsoSession(SSOSession ssoSession) {
                this.ssoSession = ssoSession;
        }

        public String getIdentityPlanName() {
                return identityPlanName;
        }

        public void setIdentityPlanName(String identityPlanName) {
                this.identityPlanName = identityPlanName;
        }

        public CircleOfTrustMemberDescriptor getMember() {
                return member;
        }

        public void setMember(CircleOfTrustMemberDescriptor member) {
                this.member = member;
        }

        public void setAccessToken(AccessToken accessToken) {
                this.accessToken = accessToken;
        }

        public AccessToken getAccessToken() {
                return accessToken;
        }

        public String getIDToken() {
                return idToken;
        }

        public void setIDToken(String idToken) {
                this.idToken = idToken;
        }

        public RefreshToken getRefreshToken() {
                return refreshToken;
        }

        public void setRefreshToken(RefreshToken refreshToken) {
                this.refreshToken = refreshToken;
        }
}
