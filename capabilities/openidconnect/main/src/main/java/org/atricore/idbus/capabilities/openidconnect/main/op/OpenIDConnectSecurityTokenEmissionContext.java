package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * Created by sgonzalez on 8/5/15.
 */
public class OpenIDConnectSecurityTokenEmissionContext extends AbstractSecurityTokenEmissionContext {

        private AccessToken accessToken;

        private RefreshToken refreshToken;

        private String idToken;

        private String issuer;

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

        public String getIssuer() {
                return issuer;
        }

        public void setIssuer(String issuer) {
                this.issuer = issuer;
        }
}
