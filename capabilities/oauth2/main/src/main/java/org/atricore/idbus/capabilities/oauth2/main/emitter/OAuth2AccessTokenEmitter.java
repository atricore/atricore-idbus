package org.atricore.idbus.capabilities.oauth2.main.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.*;
import org.atricore.idbus.capabilities.oauth2.common.util.JasonUtils;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.*;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;
import org.atricore.idbus.common.oauth._2_0.protocol.ObjectFactory;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectRoleType;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.PasswordString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;


import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AccessTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(OAuth2AccessTokenEmitter.class);

    private TokenSigner tokenSigner;

    private TokenEncrypter tokenEncrypter;

    private Random randomGenerator = new Random();

    // Default to 30 days (in minutes)
    private long rememberMeTokenValidityMins = 60L * 24L * 30L;

    // Default to 10 minutes (in seconds)
    private long tokenValiditySecs = 60L * 10L;

    private boolean rememberMeTokenEmitter = false;

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OAUTH2_TOKEN_TYPE.equals(tokenType) && !rememberMeTokenEmitter;
    }

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return super.canEmit(context, requestToken, tokenType) &&
                (!rememberMeTokenEmitter || rememberMeAttributePresent(requestToken));
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context,
                              Object requestToken,
                              String tokenType) throws SecurityTokenEmissionException {

        try {

            // If this is a remember-me emitter , only process when requested
            if (rememberMeTokenEmitter && !rememberMeAttributePresent(requestToken))
                return null;

            Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);

            // Resolve subject, using configured identity source
            subject = resolveSubject(subject);

            if (logger.isTraceEnabled())
                logger.trace("Building OAuth2 Token from " + subject);

            Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

            String ssoSessionId = null;
            int ssoSessionCount = 0;
            List<AbstractPrincipalType> proxyPrincipals = null;

            if (rstCtx instanceof AbstractSecurityTokenEmissionContext) {

                AbstractSecurityTokenEmissionContext emissionCtx = (AbstractSecurityTokenEmissionContext) rstCtx;
                ssoSessionId = emissionCtx.getSessionIndex();
                ssoSessionCount = emissionCtx.getSessionCount();

                if (emissionCtx instanceof SamlR2SecurityTokenEmissionContext) {
                    proxyPrincipals = ((SamlR2SecurityTokenEmissionContext)emissionCtx).getProxyPrincipals();
                }
            }

            // Build an access token for the subject,
            OAuth2AccessToken token = buildOAuth2AccessToken(subject, proxyPrincipals, requestToken, ssoSessionId, ssoSessionCount);

            OAuth2AccessTokenEnvelope envelope = buildOAuth2AccessTokenEnvelope(token);

            String tokenValue = marshalOAuthAccessEnvelopeToken(envelope);

            String uuid = super.uuidGenerator.generateId();
            ObjectFactory of = new ObjectFactory();
            OAuthAccessTokenType oauthToken = of.createOAuthAccessTokenType();
            oauthToken.setTokenType("bearer");
            oauthToken.setAccessToken(tokenValue);
            oauthToken.setExpiresIn(token.getExpiresOn() - System.currentTimeMillis());

            // Create a security token using the OUT artifact content.
            SecurityTokenImpl st = new SecurityTokenImpl(uuid,
                    isRememberMeTokenEmitter() ? WSTConstants.WST_OAUTH2_RM_TOKEN_TYPE : WSTConstants.WST_OAUTH2_TOKEN_TYPE,
                    oauthToken,
                    tokenValue);

            // Set token expiration
            st.setExpiresOn(token.getExpiresOn());

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

        return new OAuth2AccessTokenEnvelope(encryptAlg, sigAlg, sigValue, tokenValue, true);
    }

    protected OAuth2AccessToken buildOAuth2AccessToken(Subject subject,
                                                       List<AbstractPrincipalType> proxyPrincipals,
                                                       Object requestToken,
                                                       String ssoSessionId,
                                                       int ssoSessionCount) {

        // User
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        assert ssoUsers.size() == 1;
        OAuth2AccessToken at = new OAuth2AccessToken();
        SSOUser user = ssoUsers.iterator().next();
        at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.USERID.toString(), user.getName()));
        // Just a temporary work-around.
        at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.UNKNOWN.toString(), "UNKNOWN"));

        // Set token expiration, in millis
        long expiresIn = tokenValiditySecs * 1000L;

        // Roles
        if (!rememberMeTokenEmitter) {

            // Add all user information if this is not a remember-me token emitter

            Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);
            Set<String> usedRoles = new HashSet<String>();
            for (SSORole ssoRole : ssoRoles) {
                at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ROLE.toString(), ssoRole.getName()));
                usedRoles.add(ssoRole.getName());
            }

            Set<String> usedProps = new HashSet<String>();
            if (user.getProperties() != null) {
                for (SSONameValuePair property : user.getProperties()) {
                    usedProps.add(property.getName());

                    if (property.getValue() != null && !"".equals(property.getValue())) {

                        if (logger.isDebugEnabled())
                            logger.debug("Property: [" + property.getName() + "=" + property.getValue() + "]");

                        at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ATTRIBUTE.toString(), property.getName(), property.getValue()));
                    }
                }
            }

            // Add proxy principals (principals received from the proxied provider), but only if we don't have such a principal yet.
            if (proxyPrincipals != null) {
                for (AbstractPrincipalType principal : proxyPrincipals) {
                    if (principal instanceof SubjectAttributeType) {
                        SubjectAttributeType attr = (SubjectAttributeType) principal;
                        String name = attr.getName();

                        // Do not embed other OAuth2 related tokens
                        if (name.startsWith(WSTConstants.WST_OAUTH2_TOKEN_TYPE))
                            continue;

                        if (name.contains("oasis_wss_oauth2_token_profile_1_1#OAUTH2.0"))
                            continue;

                        if (name.contains("oasis_wss_oauth2_token_profile_1_1#RM_OAUTH2.0"))
                            continue;

                        if (name != null) {
                            int idx = name.lastIndexOf(':');
                            if (idx >= 0) name = name.substring(idx + 1);
                        }

                        String value = attr.getValue();
                        if (value != null && !"".equals(value) && !usedProps.contains(name)) {

                            if (logger.isDebugEnabled())
                                logger.debug("Property: [" + name + "=" + value + "]");

                            at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ATTRIBUTE.toString(), name, value));
                            usedProps.add(name);
                        }
                    } else if (principal instanceof SubjectRoleType) {
                        SubjectRoleType role = (SubjectRoleType) principal;
                        if (!usedRoles.contains(role.getName())) {
                            at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ROLE.toString(), role.getName()));
                            usedRoles.add(role.getName());
                        }
                    }

                }
            }

        } else {

            // Change validity if this is a remember me token emitter

            // Mark the token as used for remember-me.
            expiresIn = 1000L * 60L * rememberMeTokenValidityMins;
            at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ATTRIBUTE.name(), Constants.REMEMBERME_NS, "TRUE"));
        }

        if (logger.isDebugEnabled())
            logger.debug("Token expires in millis " + expiresIn);

        // Create some random information, to make every token unique!
        at.setTimeStamp(System.currentTimeMillis());
        at.setRnd(randomGenerator.nextInt());
        at.setExpiresOn(at.getTimeStamp() + expiresIn);

        // Set SSO Session ID
        if (ssoSessionId != null) {
            at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ATTRIBUTE.name(), "idpSsoSession", ssoSessionId));
        }

        at.getClaims().add(new OAuth2Claim(OAuth2ClaimType.ATTRIBUTE.name(), "idpSsoSessionCount", ssoSessionCount + ""));

        return at;
    }

    private String marshalOAuthAccessEnvelopeToken(OAuth2AccessTokenEnvelope envelope) throws IOException {
        return JasonUtils.marshalAccessTokenEnvelope(envelope, true);
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        throw new UnsupportedOperationException("Operation not available");
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

    public long getRememberMeTokenValidityMins() {
        return rememberMeTokenValidityMins;
    }

    public void setRememberMeTokenValidityMins(long rememberMeTokenValidityMins) {
        this.rememberMeTokenValidityMins = rememberMeTokenValidityMins;
    }

    public long getTokenValiditySecs() {
        return tokenValiditySecs;
    }

    public void setTokenValiditySecs(long tokenValiditySecs) {
        this.tokenValiditySecs = tokenValiditySecs;
    }

    public boolean isRememberMeTokenEmitter() {
        return rememberMeTokenEmitter;
    }

    public void setRememberMeTokenEmitter(boolean rememberMeTokenEmitter) {
        this.rememberMeTokenEmitter = rememberMeTokenEmitter;
    }

    protected boolean rememberMeAttributePresent(Object requestToken) {
        if (requestToken instanceof UsernameTokenType) {

            UsernameTokenType ut = (UsernameTokenType) requestToken;
            // When the requested token has a remember-me attribute, we must persist the token and set a longer expiration
            String rememberMeStr = ut.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
            if (rememberMeStr != null && Boolean.parseBoolean(rememberMeStr)) {
                return true;
            }
        } else if (requestToken instanceof PasswordString) {
            PasswordString pt = (PasswordString) requestToken;
            String rememberMeStr = pt.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
            if (rememberMeStr != null && Boolean.parseBoolean(rememberMeStr)) {
                return true;
            }
        } else if (requestToken instanceof BinarySecurityTokenType) {
            BinarySecurityTokenType bt = (BinarySecurityTokenType) requestToken;
            String rememberMeStr = bt.getOtherAttributes().get(new QName(Constants.REMEMBERME_NS));
            if (rememberMeStr != null && Boolean.parseBoolean(rememberMeStr)) {
                return true;
            }
        }

        return false;

    }
}
