package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2ClaimType;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectRoleType;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.security.auth.Subject;
import java.util.*;

/**
 * Emit an ID Token (JWT)
 */
public class IDTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(IDTokenEmitter.class);

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_ID_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {
        UsernameTokenType clientCredentials = (UsernameTokenType) requestToken;

        String authzGrant = clientCredentials.getOtherAttributes().get(OpenIDConnectConstants.AuthorizationGrant_QNAME);

        String clientId = clientCredentials.getUsername().getValue();

        Subject subject = (Subject) context.getProperty(WSTConstants.SUBJECT_PROP);

        OIDCClientInformation client = resolveClientInformation(clientId);

        IDTokenClaimsSet claimsSet = buildClaimSet(subject, null, client);

        // TODO : JWS / JCE
        /*
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet.toJWTClaimsSet());

        // Apply the HMAC
        JWSSigner signer = new MACSigner(secretKey.getEncoded());
        signedJWT.sign(signer);

        // Create JWE object with signed JWT as payload
        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).contentType("JWT").build(),
                new Payload(signedJWT));

        // Perform encryption
        jweObject.encrypt(new DirectEncrypter(secretKey.getEncoded()));
        */



        return null;
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {



        return null;
    }

    protected IDTokenClaimsSet buildClaimSet(Subject subject,
                                             List<AbstractPrincipalType> proxyPrincipals,
                                             OIDCClientInformation client) {

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        assert ssoUsers.size() == 1;
        SSOUser user = ssoUsers.iterator().next();

        String idpAlias = null;

        // sub : subject
        com.nimbusds.oauth2.sdk.id.Subject sub = new com.nimbusds.oauth2.sdk.id.Subject(user.getName());

        // iss : issuer
        Issuer iss = new Issuer(idpAlias);

        // aud : audience
        List<Audience> aud = Arrays.asList(new Audience(client.getID().getValue()));

        // iat : issue at
        Date iat = new Date();

        // exp : expires
        Date exp = new Date(System.currentTimeMillis() + 5L * 60L * 1000L); // TODO : Configure

        // Prepare JWT with claims set
        IDTokenClaimsSet claimsSet = new IDTokenClaimsSet(iss, sub, aud, exp, iat);

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

        // Add proxy principals (principals received from tho proxied provider), but only if we don't have such a principal yet.
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


    protected OIDCClientInformation resolveClientInformation(String clientId) {

        return null;
    }
}