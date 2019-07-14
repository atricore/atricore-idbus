package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import oasis.names.tc.saml._2_0.idbus.ExtAttributeListType;
import oasis.names.tc.saml._2_0.idbus.ExtendedAttributeType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ExtensionsType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.KeyUtils;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.atricore.idbus.capabilities.sts.main.*;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectRoleType;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants.OIDC_EXT_NAMESPACE;

/**
 * Emit an ID Token (JWT)
 */
public class IDTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(IDTokenEmitter.class);

    private SSOKeyResolver signer;

    private SSOKeyResolver encrypter;

    private Map<String, OIDCClientInformation> clients;

    private Map<String, OIDCClientInformation> clientsBySp;

    private Map<String, OIDCProviderMetadata> providers;

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

            Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

            OIDCClientInformation client = resolveClientInformation(clientId);
            OIDCProviderMetadata provider = resolveProviderInformation(clientId);
            if (client == null) {
                throw new SecurityTokenEmissionException("Cannot find OIDC Client " + clientId);
            }

            if (provider == null) {
                throw new SecurityTokenEmissionException("Cannot find OIDC Provider " + clientId);
            }

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

            IDTokenClaimsSet claimsSet = buildClaimSet(subject, null, extAttrs, provider, client);
            if (claimsSet == null) {
                logger.error("No claim set created for subject, probably no SSOUser principal found. " + subject);
                return null;
            }

            // Get JWE/JWS options/algorithms from client MD or OP Default settings
            JWSAlgorithm jwsAlgorithm = client.getOIDCMetadata().getIDTokenJWSAlg();
            JWEAlgorithm jweAlgorithm = client.getOIDCMetadata().getIDTokenJWEAlg();

            SignedJWT signedJWT = null;
            // Signature
            if (jwsAlgorithm != null) {
                if (JWSAlgorithm.Family.HMAC_SHA.contains(jwsAlgorithm)) {

                    SecretKey secretKey = KeyUtils.extendOrTruncateKey(client);
                    signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet.toJWTClaimsSet());

                    // Apply the HMAC
                    JWSSigner signer = new MACSigner(secretKey.getEncoded());
                    signedJWT.sign(signer);

                } else if (JWSAlgorithm.Family.EC.contains(jwsAlgorithm)) {
                    // TODO : Do we need an EC key pair ?
                    throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName());
                } else if (JWSAlgorithm.Family.ED.contains(jwsAlgorithm)) {
                    // TODO : Do we need an ED key pair ?
                    throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName());

                } else if (JWSAlgorithm.Family.RSA.contains(jwsAlgorithm)) {

                    // We have an RSA key pair as part of the IDP
                    PrivateKey privateKey = (PrivateKey) this.signer.getPrivateKey();
                    signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet.toJWTClaimsSet());
                    JWSSigner signer = new RSASSASigner(privateKey);
                    signedJWT.sign(signer);

                } else {
                    throw new SecurityTokenEmissionException("Unsupported JWS Algorithm " + jwsAlgorithm.getName());
                }
            }

            // Encryption
            if (jweAlgorithm != null) {
                // TODO :
                throw new SecurityTokenEmissionException("Unsupported JWE Algorithm " + jweAlgorithm.getName());

                // TODO for symetrical algorithms : SecretKeyDerivation.deriveSecretKey()
            }

            // TODO : JWS MUST be present ?!
            String idTokenStr = signedJWT.serialize();
            SecurityTokenImpl st = new SecurityTokenImpl<String>(uuidGenerator.generateId(),
                    WSTConstants.WST_OIDC_ID_TOKEN_TYPE,
                    idTokenStr);

            st.setSerializedContent(idTokenStr);

            // Store the Token if the context supports it.
            if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext)
                ((OpenIDConnectSecurityTokenEmissionContext)rstCtx).setIDToken(idTokenStr);

            return st;

        } catch (NoSuchAlgorithmException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (ParseException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (JOSEException e) {
            throw new SecurityTokenEmissionException(e);
        } catch (Exception e) {
            throw new SecurityTokenEmissionException(e);
        }
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        return null;
    }

    protected IDTokenClaimsSet buildClaimSet(Subject subject,
                                             List<AbstractPrincipalType> proxyPrincipals,
                                             ExtAttributeListType extAttributes,
                                             OIDCProviderMetadata provider,
                                             OIDCClientInformation client) {

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() < 1) {
            logger.error("Can't build ID Token for SimplePrincipal.  Try attaching an ID vault to your IDP/VP");
            return null;
        }

        SSOUser user = ssoUsers.iterator().next();

        // sub : subject
        com.nimbusds.oauth2.sdk.id.Subject sub = new com.nimbusds.oauth2.sdk.id.Subject(user.getName());

        // iss : issuer
        Issuer iss = new Issuer(provider.getIssuer());

        // aud : audience
        List<Audience> aud = Arrays.asList(new Audience(client.getID().getValue()));

        // iat : issue at
        Date iat = new Date();

        // exp : expires
        Date exp = new Date(System.currentTimeMillis() + 5L * 60L * 1000L); // TODO : Configure

        // Prepare JWT with claims set
        IDTokenClaimsSet claimsSet = new IDTokenClaimsSet(iss, sub, aud, exp, iat);

        // TODO : authn_time

        // nonce from TokenRequest/AuthnRequest
        if (extAttributes != null) {
            String nonce = resolveExtAttributeValue(extAttributes, "nonce");
            if (nonce != null) {
                claimsSet.setNonce(new Nonce(nonce));
            }
        }

        // TODO : acr

        // TODO : amr

        // TODO : azp

        // TODO : Attribute Profile to filter properties

        // Additional claims
        Set<String> usedProps = new HashSet<String>();
        if (user.getProperties() != null) {
            for (SSONameValuePair property : user.getProperties()) {
                usedProps.add(property.getName());
                claimsSet.setClaim(property.getName(), property.getValue());
            }
        }

        // roles
        Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);
        Set<String> usedRoles = new HashSet<String>();

        for (SSORole ssoRole : ssoRoles) {
            usedRoles.add(ssoRole.getName());
        }

        // Add proxy principals (principals received from a proxied provider), but only if we don't have such a principal yet.
        if (proxyPrincipals != null) {
            for (AbstractPrincipalType principal : proxyPrincipals) {
                if (principal instanceof SubjectAttributeType) {
                    SubjectAttributeType attr = (SubjectAttributeType) principal;
                    String name = attr.getName();
                    if (name != null) {
                        int idx = name.lastIndexOf(':');
                        if (idx >= 0) name = name.substring(idx + 1);
                    }

                    String value = attr.getValue();
                    if (!usedProps.contains(name)) {
                        claimsSet.setClaim(name, value);
                        usedProps.add(name);
                    }
                } else if (principal instanceof SubjectRoleType) {
                    SubjectRoleType role = (SubjectRoleType) principal;
                    if (!usedRoles.contains(role.getName())) {
                        usedRoles.add(role.getName());
                    }
                }

            }
        }

        // TODO : Create role claim with used roles

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
        if (requestToken instanceof UsernameTokenType ) {
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

}