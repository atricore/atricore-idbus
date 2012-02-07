package org.atricore.idbus.capabilities.atricoreid.as.main.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.atricoreid.common.*;
import org.atricore.idbus.capabilities.atricoreid.common.util.JasonUtils;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AtricoreIDAccessTokenType;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.ObjectFactory;
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
public class AtricoreIDAccessTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(AtricoreIDAccessTokenEmitter.class);

    private SSOIdentityManager identityManager;

    private TokenSigner tokenSigner;

    private TokenEncrypter tokenEncrypter;

    private Random randomGenerator = new Random();

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject when Token Type is SAMLR2!
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OAUTH2_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {

        try {
            AtricoreIDSecurityTokenEmissionContext atricoreidEmissionCtx =
                    (AtricoreIDSecurityTokenEmissionContext) context.getProperty(WSTConstants.RST_CTX);

            Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);

            // Resolve subject, using configured identity source
            subject = resolveSubject(subject);

            if (logger.isTraceEnabled())
                logger.trace("Building AtricoreID Token from " + subject);

            // Build an access token for the subject,
            AtricoreIDAccessToken token = buildAtricoreIDAccessToken(subject);

            AtricoreIDAccessTokenEnvelope envelope = buildAtricoreIDAccessTokenEnvelope(token);

            String tokenValue = marshalAtricoreIDAccessEvnelopeToken(envelope);

            String uuid = super.uuidGenerator.generateId();
            ObjectFactory of = new ObjectFactory();
            AtricoreIDAccessTokenType oauthToken = of.createAtricoreIDAccessTokenType();
            oauthToken.setTokenType("bearer");
            oauthToken.setAccessToken(tokenValue);

            // Ten minutes, make configurable!
            oauthToken.setExpiresIn(1000L * 60L * 10L);

            // Create a security token using the OUT artifact content.
            SecurityToken st = new SecurityTokenImpl(uuid, oauthToken);

            logger.debug("Created new security token [" + uuid + "] with content " + (oauthToken.getClass().getSimpleName()));

            return st;
        } catch (IOException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (AtricoreIDSignatureException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (AtricoreIDEncryptionException e) {
            throw new SecurityTokenEmissionException(e);
        }
    }

    protected AtricoreIDAccessTokenEnvelope buildAtricoreIDAccessTokenEnvelope(AtricoreIDAccessToken token) throws IOException, AtricoreIDEncryptionException, AtricoreIDSignatureException {

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

        return new AtricoreIDAccessTokenEnvelope (encryptAlg, sigAlg, sigValue, tokenValue, true);
    }

    protected AtricoreIDAccessToken buildAtricoreIDAccessToken(Subject subject) {

        // User
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        assert ssoUsers.size() == 1;
        AtricoreIDAccessToken at = new AtricoreIDAccessToken();
        SSOUser user = ssoUsers.iterator().next();
        at.getClaims().add(new AtricoreIDClaim(AtricoreIDClaimType.USERID.toString(), user.getName()));

        // Just a temporary work-around.
        at.getClaims().add(new AtricoreIDClaim(AtricoreIDClaimType.UNKNOWN.toString(), "UNKNOWN"));

        // Roles
        Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);
        for (SSORole ssoRole : ssoRoles) {
            at.getClaims().add(new AtricoreIDClaim(AtricoreIDClaimType.ROLE.toString(), ssoRole.getName()));
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
        AtricoreIDAccessToken at = new AtricoreIDAccessToken();

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

    private String marshalAtricoreIDAccessEvnelopeToken(AtricoreIDAccessTokenEnvelope envelope) throws IOException {
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
