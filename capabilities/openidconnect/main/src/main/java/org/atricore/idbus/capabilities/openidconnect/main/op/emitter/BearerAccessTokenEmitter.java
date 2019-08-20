package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import javax.security.auth.Subject;

/**
 * Emit an access Token
 */
public class BearerAccessTokenEmitter extends OIDCTokenEmitter {

    private static final Log logger = LogFactory.getLog(BearerAccessTokenEmitter.class);

    private long timeToLive = 300L;

    private Scope scope = new Scope();

    public BearerAccessTokenEmitter() {
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
        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                (WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType))) {
            return true;
        }

        // We can emit for SAML, if we have a valid ClientID
        if (WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType)) {
            return resolveClientID(context, requestToken) != null;
        }

        return false;
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {
        // Emit an AccessToken



        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null) {

            Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

            try {

                AccessToken at = new BearerAccessToken(64, timeToLive, scope);
                SecurityTokenImpl<AccessToken> st = new SecurityTokenImpl<AccessToken>(at.getValue(),
                        WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE,
                        at);

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

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
}
