package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.AuthorizationGrant;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import javax.security.auth.Subject;
import java.util.List;

/**
 * Emit an authorization grant that can be later exchanged for an Access Token
 */
public class AuthorizationCodeEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(AuthorizationCodeEmitter.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator(true);

    // Default to 10 minutes (in seconds)
    private long timeToLive = 60L * 10L;

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_AUTHZ_CODE_TYPE.equals(tokenType);
    }

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject when Token Type is SAMLR2!
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context,
                              Object requestToken,
                              String tokenType) throws SecurityTokenEmissionException {

        Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);

        // Resolve subject, using configured identity source
        subject = resolveSubject(subject);

        // Create a security token using the OUT artifact content.\

        String grantId = uuidGenerator.generateId();

        AuthorizationGrant authzGrant = new AuthorizationGrant(grantId, getSsoSessinId(context), subject,
                System.currentTimeMillis() + timeToLive * 1000L);

        SecurityTokenImpl st = new SecurityTokenImpl(grantId,
                WSTConstants.WST_OIDC_AUTHZ_CODE_TYPE,
                authzGrant,
                grantId);

        // Set token expiration
        st.setExpiresOn(authzGrant.getExpiresOn());

        // Mark it as a valid authentication grant token.
        st.setIsAuthenticationGrant(true);

        logger.debug("Created new security token [" + st.getId() + "] with content " + (authzGrant.getClass().getSimpleName()));

        return st;

    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        throw new UnsupportedOperationException("Operation not available");
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    protected String getSsoSessinId(SecurityTokenProcessingContext context) {
        Object rstCtx = context.getProperty(WSTConstants.RST_CTX);
        String ssoSessionId = null;
        List<AbstractPrincipalType> proxyPrincipals = null;
        if (rstCtx instanceof SamlR2SecurityTokenEmissionContext) {
            SamlR2SecurityTokenEmissionContext samlr2Ctx = (SamlR2SecurityTokenEmissionContext) rstCtx;
            proxyPrincipals = samlr2Ctx.getProxyPrincipals();
            ssoSessionId = samlr2Ctx.getSessionIndex();
        }
        return ssoSessionId;
    }


}
