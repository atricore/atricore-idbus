package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute.OIDCAttributeProfileMapper;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute.OneToOneAttributeProfileMapper;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.session.SSOSession;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import javax.security.auth.Subject;
import java.util.*;

/**
 * Emit an ID Token (JWT)
 */
public class IDTokenEmitter extends OIDCTokenEmitter {

    private static final Log logger = LogFactory.getLog(IDTokenEmitter.class);

    private SSOKeyResolver signer;

    private SSOKeyResolver encrypter;

    private Map<String, OIDCAttributeProfileMapper> attributeMappers;

    private long timeToLive = 300L;

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_ID_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {

        // We can emit for OIDC context with a valid subject when Token Type is OIDC_ACCESS or OIDC_ID_TOKEN
        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                (WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType) ||
                        WSTConstants.WST_OIDC_ID_TOKEN_TYPE.equals(tokenType)))
            return true;

        // We can emit for SAML, if we have a valid ClientID
        if (WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType)) {
            return resolveClientID(context, requestToken) != null;
        }

        return false;
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context,
                              Object requestToken,
                              String tokenType) throws SecurityTokenEmissionException {

        try {

            Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

            // We need an authenticated subject
            Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);
            if (subject == null) {
                logger.warn("No authenticated subject found, ignoring");
                return null;
            }

            // We need a client ID
            String clientId = resolveClientID(context, requestToken);

            if (clientId == null)
                throw new SecurityTokenEmissionException(OpenIDConnectConstants.CLIENT_ID + " not provided as token attribute");

            OIDCClientInformation client = resolveClientInformation(clientId);
            OIDCProviderMetadata opMetadata = resolveProviderInformation(clientId);
            if (client == null) {
                throw new SecurityTokenEmissionException("Cannot find OIDC Client " + clientId);
            }

            if (opMetadata == null) {
                throw new SecurityTokenEmissionException("Cannot find OIDC Provider " + clientId);
            }

            ExtAttributeListType samlExtAttrs = resolveAuthnReqExtAttrs(rstCtx);

            SSOSession session = null;

            if (rstCtx instanceof SamlR2SecurityTokenEmissionContext) {
                // We are emitting in the context of our SAML IDP/VP. We have an SSO Session object.
                SamlR2SecurityTokenEmissionContext emissionContext = (SamlR2SecurityTokenEmissionContext) rstCtx;
                session = emissionContext.getSsoSession();

            }

            IDTokenClaimsSet claimsSet = buildClaimSet(context, subject, null, session, samlExtAttrs, opMetadata, client);
            if (claimsSet == null) {
                logger.error("No claim set created for subject, probably no SSOUser principal found. " + subject);
                return null;
            }

            // Get JWE/JWS options/algorithms from client MD or OP Default settings
            JWSAlgorithm jwsAlgorithm = client.getOIDCMetadata().getIDTokenJWSAlg();
            JWEAlgorithm jweAlgorithm = client.getOIDCMetadata().getIDTokenJWEAlg();
            EncryptionMethod encMethod = client.getOIDCMetadata().getIDTokenJWEEnc();

            JWT idTokenJWT = null;
            // Encryption takes precedence over signature
            if (jweAlgorithm != null && encMethod != null) {
                idTokenJWT = encryptJWT(client, jweAlgorithm, encMethod, claimsSet.toJWTClaimsSet());
            } else if (jwsAlgorithm != null) {
                idTokenJWT = signJWT(client, this.signer.getPrivateKey(), jwsAlgorithm, claimsSet.toJWTClaimsSet());
            } else {
                throw new SecurityTokenEmissionException("Either encryption or signature MUST be enabled");
            }

            // Serialize JWT
            String idTokenStr = idTokenJWT.serialize();
            SecurityTokenImpl st = new SecurityTokenImpl<String>(uuidGenerator.generateId(),
                    WSTConstants.WST_OIDC_ID_TOKEN_TYPE,
                    idTokenStr);

            st.setSerializedContent(idTokenStr);

            // Store the Token if the context supports it.
            if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext) {
                OpenIDConnectSecurityTokenEmissionContext oidcCtx = (OpenIDConnectSecurityTokenEmissionContext) rstCtx;
                oidcCtx.setIDToken(idTokenStr);
                oidcCtx.setSubject(subject);
            }

            return st;

        } catch (Exception e) {
            throw new SecurityTokenEmissionException(e);
        }
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        return null;
    }

    protected IDTokenClaimsSet buildClaimSet(SecurityTokenProcessingContext context, Subject subject,
                                             List<AbstractPrincipalType> proxyPrincipals,
                                             SSOSession session,
                                             ExtAttributeListType extAttributes,
                                             OIDCProviderMetadata provider,
                                             OIDCClientInformation client) throws ParseException {

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() < 1) {
            logger.error("Can't build ID Token for SimplePrincipal.  Try attaching an ID vault to your IDP/VP");
            return null;
        }
        SSOUser user = ssoUsers.iterator().next();

        Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

        // This is normally an ID token issued previously from the front-channel! (contains additional information like groups, auth_time, etc)
        // May not be present
        JWT previousIdToken = getPreviousIdToken(rstCtx);
        JWTClaimsSet previousClaims = getPreviousIdTokenClaims(previousIdToken);

        // sub : subject
        com.nimbusds.oauth2.sdk.id.Subject sub = new com.nimbusds.oauth2.sdk.id.Subject(user.getName());

        // iss : issuer
        Issuer iss = new Issuer(provider.getIssuer());

        // aud : audience
        List<Audience> aud = Arrays.asList(new Audience(client.getID().getValue()));

        // iat : issue at
        Date iat = new Date();

        // exp : expires
        Date exp = new Date(System.currentTimeMillis() + timeToLive * 1000L);

        // Prepare JWT with claims set
        IDTokenClaimsSet claimsSet = new IDTokenClaimsSet(iss, sub, aud, exp, iat);

        // authn_time
        if (session != null) {
            claimsSet.setAuthenticationTime(new Date(session.getCreationTime()));
        } else if (previousClaims != null) {
            claimsSet.setClaim("auth_time", previousClaims.getClaim("auth_time"));
        } else {
            claimsSet.setAuthenticationTime(new Date());
        }

        // nonce from TokenRequest/AuthnRequest
        if (extAttributes != null) {
            String nonce = resolveExtAttributeValue(extAttributes, "nonce");
            if (nonce != null) {
                claimsSet.setNonce(new Nonce(nonce));
            }
        }

        for (SecurityToken st : context.getEmittedTokens()) {

            if (st.getNameIdentifier().equals(WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE)) {

                if (logger.isTraceEnabled())
                    logger.trace("Computing at_hash");
                AccessToken at = (AccessToken) st.getContent();

                if (at == null) {

                    if (logger.isTraceEnabled())
                        logger.trace("Computing with Serialized content");

                    at = AccessToken.parse(st.getSerializedContent());
                }

                JWSAlgorithm jwsAlgorithm = client.getOIDCMetadata().getIDTokenJWSAlg();
                claimsSet.setAccessTokenHash(AccessTokenHash.compute(at, jwsAlgorithm));
            }
        }

        // TODO : acr

        // TODO : amr

        // TODO : azp

        claimsSet = getMapper(client.getID().getValue()).toAttributes(rstCtx, subject, proxyPrincipals, claimsSet);

        return claimsSet;

    }


    public SSOKeyResolver getSigner() {
        return signer;
    }

    public void setSigner(SSOKeyResolver signer) {
        this.signer = signer;
    }

    public SSOKeyResolver getEncrypter() {
        return encrypter;
    }

    public void setEncrypter(SSOKeyResolver encrypter) {
        this.encrypter = encrypter;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public OIDCAttributeProfileMapper getMapper(String clientId) {
        OIDCAttributeProfileMapper mapper = attributeMappers.get(clientId);
        if (mapper == null) {
            mapper = new OneToOneAttributeProfileMapper();
            logger.error("No mapper defined for client: " + clientId + ".  Using one-to-one!");
        }

        return mapper;
    }


    public Map<String, OIDCAttributeProfileMapper> getAttributeMappers() {
        return attributeMappers;
    }

    public void setAttributeMappers(Map<String, OIDCAttributeProfileMapper> attributeMappers) {
        this.attributeMappers = attributeMappers;
    }

    public static JWT getPreviousIdToken(Object rstCtx) {
        if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext) {
            OpenIDConnectSecurityTokenEmissionContext emissionContext = (OpenIDConnectSecurityTokenEmissionContext) rstCtx;
            if (emissionContext.getPreviousIdToken() != null) {
                try {
                    return JWTParser.parse(emissionContext.getPreviousIdToken());
                } catch (java.text.ParseException e) {
                    logger.error("Cannot parse ID token, ignoring!" + e.getMessage());
                }
            }
        }
        return null;
    }

    public static JWTClaimsSet getPreviousIdTokenClaims(Object rstCtx) {
        return getPreviousIdTokenClaims(getPreviousIdToken(rstCtx));
    }

    public static JWTClaimsSet getPreviousIdTokenClaims(JWT previousIdToken) {
        if (previousIdToken != null) {
            try {
                return previousIdToken.getJWTClaimsSet();
            } catch (java.text.ParseException e) {
                logger.error("Cannot parse ID token, ignoring!" + e.getMessage());
            }
        }
        return null;
    }


}
