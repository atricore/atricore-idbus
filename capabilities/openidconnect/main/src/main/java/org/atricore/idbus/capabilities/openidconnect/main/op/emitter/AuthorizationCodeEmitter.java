package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import oasis.names.tc.saml._2_0.idbus.ExtendedAttributeType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ExtensionsType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.AuthorizationGrant;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState;
import org.atricore.idbus.capabilities.sts.main.*;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.util.List;

import static org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants.OIDC_EXT_NAMESPACE;

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

        String nonce = getNonce(context);
        String codeChallenge = getCodeChallenge(context);
        String codeChallengeMethod = getCodeChallengeMethod(context);

        AuthorizationGrant authzGrant = new AuthorizationGrant(grantId, getSsoSessinId(context), subject, nonce,
                codeChallenge, codeChallengeMethod,
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

    protected String getNonce(SecurityTokenProcessingContext context) {
        return getOIDCProperty(context, "nonce");
    }

    protected String getCodeChallenge(SecurityTokenProcessingContext context) {
        return getOIDCProperty(context, "code_challenge");
    }

    protected String getCodeChallengeMethod(SecurityTokenProcessingContext context) {
        return getOIDCProperty(context, "code_challenge_method");
    }

    protected String getOIDCProperty(SecurityTokenProcessingContext context, String name) {

        Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

        if (rstCtx instanceof SamlR2SecurityTokenEmissionContext) {
            SamlR2SecurityTokenEmissionContext samlr2Ctx = (SamlR2SecurityTokenEmissionContext) rstCtx;

            if (samlr2Ctx == null) {
                logger.trace("No SAML emission context found ");
                return null;
            }

            AuthenticationState authnState = samlr2Ctx.getAuthnState();
            if (authnState == null) {
                logger.trace("No Authentication State found");
                return null;
            }

            AuthnRequestType authnRequest = authnState.getAuthnRequest();
            if (authnRequest == null) {
                logger.trace("No AuthnRequest found");
                return null;
            }

            ExtensionsType extensions = authnRequest.getExtensions();
            if (extensions == null) {
                logger.trace("No SAML extensions found in AuthnRequest found");
                return null;
            }

            for (Object any : extensions.getAny()) {
                if (any instanceof JAXBElement) {

                    JAXBElement e = (JAXBElement) any;
                    if (e.getValue() instanceof ExtAttributeListType) {
                        ExtAttributeListType extAttrs = (ExtAttributeListType) e.getValue();

                        for (ExtendedAttributeType extAttr : extAttrs.getExtendedAttribute()) {
                            if (extAttr.getName().equals(OIDC_EXT_NAMESPACE + ":" + name))
                                return extAttr.getValue();
                        }
                    }
                }

            }

        }
        return null;

    }


}
