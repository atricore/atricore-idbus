package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import net.minidev.json.JSONArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import javax.security.auth.Subject;
import java.util.Date;
import java.util.Map;


public class JWTAccessTokenEmitter extends OIDCTokenEmitter {


    private static final Log logger = LogFactory.getLog(JWTAccessTokenEmitter.class);

    private long timeToLive = 300L;

    private Scope scope = new Scope();

    private SSOKeyResolver signer;

    private boolean includeIdTokenClaims = false;

    public JWTAccessTokenEmitter() {
        scope.add(OIDCScopeValue.OPENID);
    }

    public SSOKeyResolver getSigner() {
        return signer;
    }

    public void setSigner(SSOKeyResolver signer) {
        this.signer = signer;
    }

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public boolean canEmit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        // We can emit for any context with a valid subject when Token Type is OIDC_ACCESS!
        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                (WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType))) {
            return true;

        }

        // We can emit for SAML, if we have a valid ClientID
        if (WSTConstants.WST_SAMLR2_TOKEN_TYPE.equals(tokenType)) {
            return resolveClientID(context, requestToken) != null;
        }

        return false;
    }


    @Override
    public SecurityToken emit(SecurityTokenProcessingContext context, Object requestToken, String tokenType) throws SecurityTokenEmissionException {
        // Emit an AccessToken

        Object rstCtx = context.getProperty(WSTConstants.RST_CTX);

        if (context.getProperty(WSTConstants.SUBJECT_PROP) != null) {

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

                OIDCClientInformation client = resolveClientInformation(clientId);
                OIDCProviderMetadata opMetadata = resolveProviderInformation(clientId);
                if (client == null) {
                    throw new SecurityTokenEmissionException("Cannot find OIDC Client " + clientId);
                }

                if (opMetadata == null) {
                    throw new SecurityTokenEmissionException("Cannot find OIDC Provider " + clientId);
                }

                // Get JWE/JWS options/algorithms from client MD or OP Default settings
                JWSAlgorithm jwsAlgorithm = client.getOIDCMetadata().getIDTokenJWSAlg();

                // iss : issuer
                Issuer iss = new Issuer(opMetadata.getIssuer());

                // aud : audience
                JSONArray audList = new JSONArray();
                audList.add(client.getID().getValue());

                // exp
                Date exp = new Date(System.currentTimeMillis() + (timeToLive * 1000L));

                // JTI
                String jti = uuidGenerator.generateId();

                JWT previousIdToken = getPreviousIdToken(rstCtx);
                JWTClaimsSet previousClaims = getPreviousIdTokenClaims(previousIdToken);

                // Claims
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder().
                        issuer(iss.getValue()).
                        expirationTime(exp).
                        claim("aud", audList);

                if (includeIdTokenClaims && previousClaims != null) {
                    for (Map.Entry<String, Object> entry : previousClaims.getClaims().entrySet()) {
                        builder.claim(entry.getKey(), entry.getValue());
                    }
                }

                JWTClaimsSet claimSet = builder.
                        jwtID(jti).
                        build();

                JWT token = signJWT(client, this.signer.getPrivateKey(), jwsAlgorithm, claimSet);

                String jwtTokenStr = token.serialize();
                AccessToken at = new BearerAccessToken(jwtTokenStr, timeToLive, scope);

                SecurityTokenImpl<AccessToken> st = new SecurityTokenImpl<AccessToken>(jti,
                        WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE,
                        at);
                st.setExpiresOn(exp.getTime());

                if (rstCtx instanceof OpenIDConnectSecurityTokenEmissionContext) {
                    // We're issuing an access token for OpenID, and not in the context of another protocol
                    OpenIDConnectSecurityTokenEmissionContext oidcCtx = (OpenIDConnectSecurityTokenEmissionContext) rstCtx;
                    oidcCtx.setAccessToken(at);
                    oidcCtx.setSubject(subject);

                } else {
                    // We're issuing an access token in the context of another protocol, probably SAML
                }

                st.setSerializedContent(at.getValue());

                return st;

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new SecurityTokenEmissionException(e);
            }
        } else {
            logger.warn("No authenticated subject found, ignoring");
        }

        throw new SecurityTokenEmissionException("No Subject authenticated, aborting");
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        return null;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isIncludeIdTokenClaims() {
        return includeIdTokenClaims;
    }

    public void setIncludeIdTokenClaims(boolean includeIdTokenClaims) {
        this.includeIdTokenClaims = includeIdTokenClaims;
    }
}
