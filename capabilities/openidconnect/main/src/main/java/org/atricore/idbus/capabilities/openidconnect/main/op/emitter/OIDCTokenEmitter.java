package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.jose.SecretKeyDerivation;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import oasis.names.tc.saml._2_0.idbus.ExtendedAttributeType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ExtensionsType;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.KeyUtils;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sts.main.*;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBElement;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Map;

import static org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants.OIDC_EXT_NAMESPACE;

public abstract class OIDCTokenEmitter extends AbstractSecurityTokenEmitter {

    private Map<String, OIDCClientInformation> clients;

    private Map<String, OIDCClientInformation> clientsBySp;

    private Map<String, OIDCProviderMetadata> providers;



    public ExtAttributeListType resolveAuthnReqExtAttrs(Object rstCtx) {
        ExtAttributeListType extAttrs = null;
        if (rstCtx instanceof  SamlR2SecurityTokenEmissionContext) {
            SamlR2SecurityTokenEmissionContext saml2Ctx = (SamlR2SecurityTokenEmissionContext) rstCtx;
            AuthnRequestType authnReq = saml2Ctx.getAuthnState().getAuthnRequest();
            if (authnReq != null) {
                ExtensionsType exts = authnReq.getExtensions();
                for (Object ext : exts.getAny()) {
                    if (ext instanceof JAXBElement) {
                        JAXBElement jaxbExt = (JAXBElement) ext;
                        if (jaxbExt.getValue() instanceof ExtAttributeListType)
                            extAttrs = (ExtAttributeListType) jaxbExt.getValue();
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
            EncryptedJWT encryptedJWT = new EncryptedJWT(new JWEHeader(jweAlgorithm, encMethod), claimsSet);
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
            encryptedJWT.encrypt(jwtEncrypter);
            return encryptedJWT;

        } catch (JOSEException e) {
            throw new SecurityTokenEmissionException("Unsupported JWE Algorithm/Method " + jweAlgorithm.getName() + "/" + encMethod.getName() + ". " + e.getMessage(), e);
        }
    }

    protected SignedJWT signJWT(OIDCClientInformation client, Key key, JWSAlgorithm jwsAlgorithm, JWTClaimsSet claimsSet) {

        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet);
            JWSSigner jwtSigner = null;

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
                PrivateKey privateKey = (PrivateKey) key;
                jwtSigner = new RSASSASigner(privateKey);

            } else {
                throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName());
            }

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
