package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
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
 * Emit an access Token
 */
public class AccessTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(AccessTokenEmitter.class);

    private Map<String, OIDCClientInformation> clients;

    private long lifetimeInSecs = 300L;

    private Scope scope = new Scope();

    public AccessTokenEmitter() {
        scope.add(OIDCScopeValue.OPENID);
    }

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject when Token Type is OIDC_ACCESS!
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                (WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType) || WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType));
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {
        // Emit an AccessToken

        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null) {

            try {
                AccessToken at = new BearerAccessToken(64, lifetimeInSecs, scope);
                SecurityTokenImpl<AccessToken> st = new SecurityTokenImpl<AccessToken>(at.getValue(),
                        WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE,
                        at);

                Object rstCtx = context.getProperty(WSTConstants.RST_CTX);
                if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext) {
                    // We're issuing an access token for OpenID, and not in the context of another protocol
                    OpenIDConnectSecurityTokenEmissionContext oidcCtx = (OpenIDConnectSecurityTokenEmissionContext) rstCtx;

                    Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);
                    oidcCtx.setAccessToken(at);
                    oidcCtx.setSubject(subject);

                } else {
                    // We're issuing an access token in the context of another protocol, probably SAML
                }

                st.setSerializedContent(at.getValue());

                // Store the Token if the context supports it.
                if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext)
                    ((OpenIDConnectSecurityTokenEmissionContext)rstCtx).setAccessToken(at);

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
