package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.KeyUtils;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
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
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.Subject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Emit an ID Token (JWT)
 */
public class IDTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(IDTokenEmitter.class);

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


            IDTokenClaimsSet claimsSet = buildClaimSet(subject, null, provider, client);
            if (claimsSet == null) {
                logger.error("No claim set created for subject, probably no SSOUser principal found. " + subject);
                return null;
            }

            // TODO : Get JWE/JWS options/algorithms from client MD or OP Default settings
            SecretKey secretKey = KeyUtils.getKey(client);
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet.toJWTClaimsSet());

            // Apply the HMAC
            JWSSigner signer = new MACSigner(secretKey.getEncoded());
            signedJWT.sign(signer);

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
        //
        // TODO : nonce Return nonce from TokenRequest/AuthnRequest

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

}