package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.*;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.jose.SecretKeyDerivation;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import oasis.names.tc.saml._2_0.idbus.ExtendedAttributeType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ExtensionsType;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.KeyUtils;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute.OIDCAttributeProfileMapper;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute.OneToOneAttributeProfileMapper;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.*;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.session.SSOSession;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.*;

import static org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants.OIDC_EXT_NAMESPACE;

public abstract class OIDCTokenEmitter extends AbstractSecurityTokenEmitter {

    public static final Log logger = LogFactory.getLog(OIDCTokenEmitter.class);

    private Map<String, OIDCClientInformation> clients;

    private Map<String, OIDCClientInformation> clientsBySp;

    private Map<String, OIDCProviderMetadata> providers;

    private Map<String, OIDCAttributeProfileMapper> attributeMappers = new HashMap<String, OIDCAttributeProfileMapper>();

    protected IDTokenClaimsSet buildClaimSet(SecurityTokenProcessingContext context,
                                             OIDCProviderMetadata provider,
                                             OIDCClientInformation client,
                                             long timeToLive) throws ParseException {

        // We need an authenticated subject
        Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);
        if (subject == null) {
            logger.warn("No authenticated subject found, ignoring");
            return null;
        }

        // Get username, principal may be SSOUser or SimplePrinciapl
        String username = null;
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() < 1) {
            // We are a proxy, without an attached identity source
            if (logger.isDebugEnabled())
                logger.debug("Proxy ID Token emitter without identity source.  Using SimplePrincipal/PreviousClaims");
            Set<SimplePrincipal> users = subject.getPrincipals(SimplePrincipal.class);
            username = users.iterator().next().getName();
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Proxy ID Token emitter with identity source.  Using local SSOUser");
            username = ssoUsers.iterator().next().getName();
        }

        // Get SSOSession, if emittion is during SAML 2
        Object rstCtx = context.getProperty(WSTConstants.RST_CTX);
        List<AbstractPrincipalType> proxyPrincipals =  null;
        SSOSession session = null;
        if (rstCtx instanceof SamlR2SecurityTokenEmissionContext) {
            // We are emitting in the context of our SAML IDP/VP. We have an SSO Session object.
            SamlR2SecurityTokenEmissionContext emissionContext = (SamlR2SecurityTokenEmissionContext) rstCtx;
            session = emissionContext.getSsoSession();
            proxyPrincipals = emissionContext.getProxyPrincipals();

        }

        // This is normally an ID token issued previously from the front-channel! (contains additional information like groups, auth_time, etc)
        // May not be present
        JWT previousIdToken = getPreviousIdToken(rstCtx);
        JWTClaimsSet previousClaims = getPreviousIdTokenClaims(previousIdToken);

        // sub : subject
        com.nimbusds.oauth2.sdk.id.Subject sub = new com.nimbusds.oauth2.sdk.id.Subject(username);

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
        String nonce = null;
        ExtAttributeListType samlExtAttrs = resolveAuthnReqExtAttrs(rstCtx);
        if (samlExtAttrs != null) {
            nonce = resolveExtAttributeValue(samlExtAttrs, "nonce");
        }

        if (nonce == null && previousClaims != null && previousClaims.getClaim("nonce") != null) {
            nonce = (String) previousClaims.getClaim("nonce");
        }

        if (nonce != null)
            claimsSet.setNonce(new Nonce(nonce));

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

        // Reuse claims
        if (previousClaims != null) {
            Map<String, Object> previousClaimsMap = previousClaims.getClaims();
            for (String claimName : previousClaimsMap.keySet()) {
                Object claimValue = previousClaimsMap.get(claimName);
                // add to current if no value exists!
                if (claimsSet.getClaim(claimName) == null) {
                    claimsSet.setClaim(claimName, claimValue);
                }
            }
        } else {
            claimsSet = getMapper(client.getID().getValue()).toAttributes(rstCtx, subject, proxyPrincipals, claimsSet);
        }


        return claimsSet;

    }

    public OIDCAttributeProfileMapper getMapper(String clientId) {
        OIDCAttributeProfileMapper mapper = attributeMappers.get(clientId);
        if (mapper == null) {
            mapper = new OneToOneAttributeProfileMapper();
            logger.warn("No mapper defined for client: " + clientId + ".  Using one-to-one!");
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




    public ExtAttributeListType resolveAuthnReqExtAttrs(Object rstCtx) {
        ExtAttributeListType extAttrs = null;
        if (rstCtx instanceof  SamlR2SecurityTokenEmissionContext) {
            SamlR2SecurityTokenEmissionContext saml2Ctx = (SamlR2SecurityTokenEmissionContext) rstCtx;
            AuthnRequestType authnReq = saml2Ctx.getAuthnState().getAuthnRequest();
            if (authnReq != null) {
                ExtensionsType exts = authnReq.getExtensions();
                if (exts != null) {
                    for (Object ext : exts.getAny()) {
                        if (ext instanceof JAXBElement) {
                            JAXBElement jaxbExt = (JAXBElement) ext;
                            if (jaxbExt.getValue() instanceof ExtAttributeListType)
                                extAttrs = (ExtAttributeListType) jaxbExt.getValue();
                        }
                    }
                }
            }
        }
        return extAttrs;
    }



    public Map<String, OIDCClientInformation> getClients() {
        return clients;
    }

    public void setClients(Map<String, OIDCClientInformation> clients) {
        this.clients = clients;
    }

    protected OIDCClientInformation resolveClientInformation(String clientId) {
        return clients.get(clientId);
    }

    protected OIDCProviderMetadata resolveProviderInformation(String clientId) {
        return providers.get(clientId);
    }


    public Map<String, OIDCClientInformation> getClientsBySp() {
        return clientsBySp;
    }

    public void setClientsBySp(Map<String, OIDCClientInformation> clientsBySp) {
        this.clientsBySp = clientsBySp;
    }

    public Map<String, OIDCProviderMetadata> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, OIDCProviderMetadata> providers) {
        this.providers = providers;
    }

    protected String resolveClientID(SecurityTokenProcessingContext context, Object requestToken) {

        // See if we have a client ID as a token attribute.
        String clientId = null;
        if (requestToken instanceof UsernameTokenType) {
            UsernameTokenType userCredentials = (UsernameTokenType) requestToken;
            clientId = userCredentials.getOtherAttributes().get(OpenIDConnectConstants.CLIENT_ID);
        } else if (requestToken instanceof BinarySecurityTokenType) {
            BinarySecurityTokenType userCredentials = (BinarySecurityTokenType) requestToken;
            clientId = userCredentials.getOtherAttributes().get(OpenIDConnectConstants.CLIENT_ID);
        }

        if (clientId != null)
            return clientId;

        // Get clientId from SP alias
        SecurityTokenEmissionContext ctx = (SecurityTokenEmissionContext) context.getProperty(WSTConstants.RST_CTX);
        if (ctx instanceof SamlR2SecurityTokenEmissionContext) {
            // We are emitting in a SAML assertion emisison context
            SamlR2SecurityTokenEmissionContext saml2Ctx = (SamlR2SecurityTokenEmissionContext) ctx;

            // This is the SAML SP, it should be an OIDC Relaying Party proxy, or we can't emit.
            CircleOfTrustMemberDescriptor sp = saml2Ctx.getMember();

            if (clientsBySp == null)
                return null;

            OIDCClientInformation clientInfo = clientsBySp.get(sp.getAlias());

            if (clientInfo != null)
                return clientInfo.getID().getValue();
        }

        return null;

    }

    /**
     * Gets an OIDC attribute value.  It will add the OIDC Namespace to the provided name when seaching for values
     */
    protected String resolveExtAttributeValue(ExtAttributeListType extAttrs, String name) {
        if (extAttrs == null)
            return null;

        for (ExtendedAttributeType attr : extAttrs.getExtendedAttribute()) {
            if (attr.getName().equals(OIDC_EXT_NAMESPACE + ":" + name))
                return attr.getValue();
        }

        return null;
    }

    protected JWK resolveClientEncryptionKey(ClientInformation client, KeyUse keyUse, KeyType keyType) {
        JWKSet keys = client.getMetadata().getJWKSet();

        for (JWK key : keys.getKeys()) {
            if (key.getKeyUse().equals(keyUse) && key.getKeyType().equals(keyType))
                return key;
        }

        return null;
    }


    protected EncryptedJWT encryptJWT(OIDCClientInformation client, JWEAlgorithm jweAlgorithm, EncryptionMethod encMethod, JWTClaimsSet claimsSet) {
        try {

            JWEEncrypter jwtEncrypter = null;

            if (JWEAlgorithm.Family.RSA.contains(jweAlgorithm)) {

                // We encrypt with the client's public key
                // TODO : get RSA Public key from certificate provided in the console!
                        /*
                        JWK publicKey = resolveClientEncryptionKey(client, KeyUse.ENCRYPTION, KeyType.forAlgorithm(jweAlgorithm));
                        RSAPublicKey rsaPublicKey = null;
                        RSAEncrypter jwtEncrypter = new RSAEncrypter(rsaPublicKey);
                        jwtEncrypter .getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());
                        encryptedJWT.encrypt(jwtEncrypter);

                         */

                throw new SecurityTokenEmissionException("Unsupported Encryption Algorithm " + jweAlgorithm.getName());

            } else if (JWEAlgorithm.Family.AES_GCM_KW.contains(jweAlgorithm) ||
                    JWEAlgorithm.Family.AES_KW.contains(jweAlgorithm)) {

                SecretKey key = SecretKeyDerivation.deriveSecretKey(client.getSecret(), jweAlgorithm, encMethod);
                jwtEncrypter = new AESEncrypter(key);

            } else {
                // TODO : Support other algorithms
                throw new SecurityTokenEmissionException("Unsupported Encryption Algorithm " + jweAlgorithm.getName());
            }

            jwtEncrypter .getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());

            EncryptedJWT encryptedJWT = new EncryptedJWT(new JWEHeader(jweAlgorithm, encMethod), claimsSet);
            encryptedJWT.encrypt(jwtEncrypter);
            return encryptedJWT;

        } catch (JOSEException e) {
            throw new SecurityTokenEmissionException("Unsupported JWE Algorithm/Method " + jweAlgorithm.getName() + "/" + encMethod.getName() + ". " + e.getMessage(), e);
        }
    }

    protected SignedJWT signJWT(OIDCClientInformation client, Key key, JWSAlgorithm jwsAlgorithm, JWTClaimsSet claimsSet) {

        try {

            JWSSigner jwtSigner = null;
            String kid = null;
            if (JWSAlgorithm.Family.HMAC_SHA.contains(jwsAlgorithm)) {
                SecretKey secretKey = KeyUtils.extendOrTruncateKey(client);
                jwtSigner = new MACSigner(secretKey.getEncoded());

            } else if (JWSAlgorithm.Family.EC.contains(jwsAlgorithm)) {

                PrivateKey privateKey = (PrivateKey) key;
                Curve curve = null;
                if (jwsAlgorithm.equals(JWSAlgorithm.ES256))
                    curve = Curve.P_256;
                else if (jwsAlgorithm.equals(JWSAlgorithm.ES256K))
                    curve = Curve.P_256K;
                else if (jwsAlgorithm.equals(JWSAlgorithm.ES384))
                    curve = Curve.P_384;
                else if (jwsAlgorithm.equals(JWSAlgorithm.ES512))
                    curve = Curve.P_521;

                jwtSigner = new ECDSASigner(privateKey, curve);

            } else if (JWSAlgorithm.Family.ED.contains(jwsAlgorithm)) {
                // TODO : Do we need an ED key pair
                /*
                // Generate a key pair with Ed25519 curve
                OctetKeyPair jwk = new OctetKeyPairGenerator(Curve.Ed25519).keyID("123").generate();
                OctetKeyPair publicJWK = jwk.toPublicJWK();

                // Create the EdDSA signer
                JWSSigner signer = new Ed25519Signer(jwk);
                */
                throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName());

            } else if (JWSAlgorithm.Family.RSA.contains(jwsAlgorithm)) {
                // We sign with our private key
                // We have an RSA key pair as part of the IDP
                kid = client.getID().getValue() + "-sign";
                PrivateKey privateKey = (PrivateKey) key;
                jwtSigner = new RSASSASigner(privateKey);

            } else {
                throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName());
            }

            SignedJWT signedJWT = kid != null ?
                    new SignedJWT(new JWSHeader.Builder(jwsAlgorithm).keyID(kid).build(), claimsSet) :
                    new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet);
            signedJWT.sign(jwtSigner);
            return signedJWT;

        } catch (NoSuchAlgorithmException e) {
            throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName() + ". " + e.getMessage(), e);
        } catch (KeyLengthException e) {
            throw new SecurityTokenEmissionException("Invalid key length. " + e.getMessage(), e);
        } catch (JOSEException e) {
            throw new SecurityTokenEmissionException("Error signing token. " + e.getMessage(), e);
        }
    }
}
