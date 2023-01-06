package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectSecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute.OIDCAttributeProfileMapper;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.*;
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

            IDTokenClaimsSet claimsSet = buildClaimSet(context, opMetadata, client, this.timeToLive);
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
            Object rstCtx = context.getProperty(WSTConstants.RST_CTX);
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


}
