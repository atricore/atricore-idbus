package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import javax.security.auth.Subject;
import java.util.Map;

/**
 * Emit a refresh token that can later be used to renew an access token
 */
public class RefreshTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(RefreshTokenEmitter.class);

    private Map<String, OIDCClientInformation> clients;


    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject when Token Type is OIDC_ACCESS!
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                (WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE.equals(tokenType) || WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType));
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {
        // Emit an AccessToken

        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null) {

            try {
                RefreshToken rt = new RefreshToken(32);
                SecurityTokenImpl<RefreshToken> st = new SecurityTokenImpl<RefreshToken>(rt.getValue(),
                        WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE,
                        rt);

                Object rstCtx = context.getProperty(WSTConstants.RST_CTX);
                if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext) {
                    // We're issuing an access token for OpenID, and not in the context of another protocol
                    OpenIDConnectSecurityTokenEmissionContext oidcCtx = (OpenIDConnectSecurityTokenEmissionContext) rstCtx;

                    Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);
                    oidcCtx.setRefreshToken(rt);
                    oidcCtx.setSubject(subject);

                } else {
                    // We're issuing a refresh token in the context of another protocol, probably SAML
                }

                st.setSerializedContent(rt.getValue());
                return st;

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new SecurityTokenEmissionException(e);
            }
        } else {
            logger.warn("No authenticated subject found, ignoring");
        }

        throw new SecurityTokenEmissionException("No Subject authenticated, aborting");
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        return null;
    }

    public Map<String, OIDCClientInformation> getClients() {
        return clients;
    }

    public void setClients(Map<String, OIDCClientInformation> clients) {
        this.clients = clients;
    }
}