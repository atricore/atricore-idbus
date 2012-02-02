package org.atricore.idbus.capabilities.oauth2.main.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.*;
import org.atricore.idbus.capabilities.oauth2.common.util.JasonUtils;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;
import org.atricore.idbus.common.oauth._2_0.protocol.ObjectFactory;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.planning.IdentityArtifact;


import javax.security.auth.Subject;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AccessTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(OAuth2AccessTokenEmitter.class);

    private SSOIdentityManager identityManager;

    private TokenSigner tokenSigner;

    private TokenEncrypter tokenEncrypter;

    private Random randomGenerator = new Random();

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OAUTH2_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context,
                              Object requestToken,
                              String tokenType) throws SecurityTokenEmissionException {

        try {

            Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);

            // Resolve subject, using configured identity source
            subject = resolveSubject(subject);

            if (logger.isTraceEnabled())
                logger.trace("Building OAuth2 Token from " + subject);

            // Build an access token for the subject,
            OAuth2AccessToken token = buildOAuth2AccessToken(subject);

            OAuth2AccessTokenEnvelope envelope = buildOAuth2AccessTokenEnvelope(token);

            String tokenValue = marshalOAuthAccessEvnelopeToken(envelope);

            String uuid = super.uuidGenerator.generateId();
            ObjectFactory of = new ObjectFactory();
            OAuthAccessTokenType oauthToken = of.createOAuthAccessTokenType();
            oauthToken.setTokenType("bearer");
            oauthToken.setAccessToken(tokenValue);

            // Ten minutes, TODO : make configurable!
            oauthToken.setExpiresIn(1000L * 60L * 10L);

            // Create a security token using the OUT artifact content.
            SecurityToken st = new SecurityTokenImpl(uuid,
                    WSTConstants.WST_OAUTH2_TOKEN_TYPE,
                    oauthToken,
                    tokenValue);

            logger.debug("Created new security token [" + uuid + "] with content " + (oauthToken.getClass().getSimpleName()));

            return st;
        } catch (IOException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (OAuth2SignatureException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (OAuth2EncryptionException e) {
            throw new SecurityTokenEmissionException(e);
        }
    }

    protected OAuth2AccessTokenEnvelope buildOAuth2AccessTokenEnvelope(OAuth2AccessToken token) throws IOException, OAuth2EncryptionException, OAuth2SignatureException {

        // Build and deflate
        String tokenValue = JasonUtils.marshalAccessToken(token);
        tokenValue = JasonUtils.deflate(tokenValue, true);

        // Encrypt
        String encryptAlg = null;
        if (tokenEncrypter != null) {
            tokenValue = tokenEncrypter.encrypt(tokenValue);
            encryptAlg = tokenEncrypter.getEncryptAlg();
        }

        // Sign
        String sigValue = null;
        String sigAlg = null;
        if (tokenSigner != null) {
            sigValue = tokenSigner.signToken(tokenValue);
            sigAlg = tokenSigner.getSignAlg();
        }

        return new OAuth2AccessTokenEnvelope (encryptAlg, sigAlg, sigValue, tokenValue, true);
    }

    protected OAuth2AccessToken buildOAuth2AccessToken(Subject subject) {

        // User
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        assert ssoUsers.size() == 1;
        OAuth2AccessToken at = new OAuth2AccessToken();
        SSOUser user = ssoUsers.iterator().next();
        at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.USERID.toString(), user.getName()));

        // Just a temporary work-around.
        at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.UNKNOWN.toString(), "UNKNOWN"));

        // Roles
        Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);
        for (SSORole ssoRole : ssoRoles) {
            at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ROLE.toString(), ssoRole.getName()));
        }

        // Create some randon information, to make every token different!
        at.setTimeStamp(System.currentTimeMillis());
        // is this thread-safe ?!
        at.setRnd(randomGenerator.nextInt());

        return at;
    }

    /**
     * TODO : Use identity planning, and reuse some STS actions!
     *
     * @param subject
     * @return
     */
    protected Subject resolveSubject(Subject subject) {
        OAuth2AccessToken at = new OAuth2AccessToken();

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        Set<SimplePrincipal> simplePrincipals = subject.getPrincipals(SimplePrincipal.class);

        if (ssoUsers != null && ssoUsers.size() > 0) {

            if (logger.isDebugEnabled())
                logger.debug("Emitting token for Subject with SSOUser");

            // Build Subject
            // s = new Subject(true, s.getPrincipals(), s.getPrivateCredentials(), s.getPublicCredentials());
        } else {

            try {

                // Resolve SSOUser
                SimplePrincipal sp = simplePrincipals.iterator().next();
                String username = sp.getName();

                SSOIdentityManager idMgr = getIdentityManager();
                if (idMgr == null)
                    throw new IllegalStateException("SSOIdentityManager not configured for plan " + getClass().getSimpleName());

                if (logger.isTraceEnabled())
                    logger.trace("Resolving SSOUser for " + username);

                Set<Principal> principals = new HashSet<Principal>();

                // Find SSOUser principal
                SSOUser ssoUser = idMgr.findUser(username);
                principals.add(ssoUser);

                // Find SSORole principals
                SSORole[] ssoRoles = idMgr.findRolesByUsername(username);
                principals.addAll(Arrays.asList(ssoRoles));

                // Use existing SSOPolicyEnforcement principals
                Set<SSOPolicyEnforcementStatement> ssoPolicies = subject.getPrincipals(SSOPolicyEnforcementStatement.class);
                if (ssoPolicies != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding " + ssoPolicies.size() + " SSOPolicyEnforcement principals ");

                    principals.addAll(ssoPolicies);
                }

                // Build Subject
                subject = new Subject(true, principals, subject.getPublicCredentials(), subject.getPrivateCredentials());

            } catch (Exception e) {
                throw new SecurityTokenEmissionException(e);
            }


        }

        return subject;
    }

    private String marshalOAuthAccessEvnelopeToken(OAuth2AccessTokenEnvelope envelope) throws IOException {
        return JasonUtils.marshalAccessTokenEnvelope(envelope, true);
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        throw new UnsupportedOperationException("Operatino not available");
    }

    public SSOIdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setIdentityManager(SSOIdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public TokenSigner getTokenSigner() {
        return tokenSigner;
    }

    public void setTokenSigner(TokenSigner tokenSigner) {
        this.tokenSigner = tokenSigner;
    }

    public TokenEncrypter getTokenEncrypter() {
        return tokenEncrypter;
    }

    public void setTokenEncrypter(TokenEncrypter tokenEncrypter) {
        this.tokenEncrypter = tokenEncrypter;
    }
}
