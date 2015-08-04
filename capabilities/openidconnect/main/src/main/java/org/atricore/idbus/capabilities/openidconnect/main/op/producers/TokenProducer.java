package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.*;
import java.util.Arrays;
import java.util.Date;

/**
 *
 */
public class TokenProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public TokenProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        TokenRequest tokenRequest = (TokenRequest) in.getMessage().getContent();
        MediationState state = in.getMessage().getState();


        OpenIDConnectBinding binding = OpenIDConnectBinding.asEnum(endpoint.getBinding());
        EndpointDescriptor errorEndpoint = new EndpointDescriptorImpl("rp-error",
                OpenIDConnectService.TokenService.toString(),
                binding.getValue(), null, null);

        // If we have an authorization_code , the state MUST have the proper alternate key.
        AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();
        if (grant instanceof AuthorizationCodeGrant) {
            String expectedAuthzCode = state.getLocalState().getAlternativeId("authorization_code");
            if (expectedAuthzCode == null) {
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "authorization_code:n/a");
            } else {
                AuthorizationCodeGrant authzGrant = (AuthorizationCodeGrant) grant;
                AuthorizationCode authzCode = authzGrant.getAuthorizationCode();
                if (authzCode == null || !authzCode.getValue().equals(expectedAuthzCode))
                    throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "authorization_code:" + authzCode);
            }
        }

        // Validate the incoming request
        OIDCClientInformation clientInfo = validateRequest(exchange, tokenRequest);

        // Depending on the authorization grant, we perform different emissions
        // TODO : Support other grant types
        if (grant.getType().equals(GrantType.JWT_BEARER) ||
                grant.getType().equals(JWT_BEARER_PWD)) {

            JWTBearerGrant jwtBearerGrant = (JWTBearerGrant) grant;

            JWT assertion = jwtBearerGrant.getJWTAssertion();
            ReadOnlyJWTClaimsSet claimsSet = assertion.getJWTClaimsSet();

            // Is this bearer with pwd ?
            if (claimsSet.getClaim("cred") != null) {
                // Now, if the cred claim is available, and the grant is supported, use basic auth for the user.
                String username = claimsSet.getSubject();
                String password = (String) claimsSet.getClaim("cred");

                //emitTokensFrom
            }
        }



        // Issue Access Token

        // Issue Refresh Token (optional)

        // Issue ID Token

        TokenResponse tokenResponse = buildAccessTokenResponse();

        // Send response back (this is a back-channel request)
        /*
        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                tokenResponse,
                "AccessTokenResponse",
                null,
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);
        */



    }

    /**
     * This validates the received Token request.
     *
     * Verify authorization grant
     *
     * @param exchange
     * @param tokenRequest
     * @throws OpenIDConnectException
     */
    protected OIDCClientInformation validateRequest(CamelMediationExchange exchange, TokenRequest tokenRequest)
            throws OpenIDConnectException {

        // Authenticate Client:
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState state = in.getMessage().getState();

        long now = System.currentTimeMillis();

        // ----------------------------------------------
        // Client ID (also taken from request parameter)
        // ----------------------------------------------
        ClientID clientID = tokenRequest.getClientAuthentication() != null ?
                tokenRequest.getClientAuthentication().getClientID() : tokenRequest.getClientID();

        if (clientID == null)
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_REQUEST, "client_id:n/a");

        if (logger.isDebugEnabled())
            logger.debug("Processing TokenRequest for " + clientID.getValue());

        OIDCClientInformation clientInfo  = resolveClient(clientID);
        if (clientInfo == null) {
            throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "client_id:" + clientID.getValue());
        }

        if (logger.isDebugEnabled())
            logger.debug("Processing TokenRequest for " + clientID.getValue());
        ClientID expectedClientId = clientInfo.getID();

        // ----------------------------------------------
        // Authenticate Client
        // ----------------------------------------------
        authenticateClient(clientInfo, tokenRequest);

        // ----------------------------------------------
        // Verify that the Client support the Authorization Grant
        // ----------------------------------------------
        AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();
        if (grant == null)
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "grant_type:n/a");
        if (clientInfo.getOIDCMetadata().getGrantTypes().contains(grant.getType())) {
            if (logger.isDebugEnabled())
                logger.debug("Invalid Authorization Grant ["+tokenRequest.getAuthorizationGrant()+"] for Client " + expectedClientId.getValue());

            throw new OpenIDConnectProviderException(OAuth2Error.UNSUPPORTED_GRANT_TYPE, "grant_type:" + grant.getType().getValue());
        }


        // Emit tokens from Grant
        AccessToken at = null;
        RefreshToken rt = null;
        JWT idToken = null;

        // ----------------------------------------------
        // Make Grant specific validations
        // ----------------------------------------------
        if (grant.getType().equals(GrantType.AUTHORIZATION_CODE)) {

            at = emitAccessTokenFromAuthzCode(clientInfo, (AuthorizationCodeGrant) grant);
            idToken = emitIDTokenFromAuthzCode(clientInfo, (AuthorizationCodeGrant) grant);

        } else if (grant.getType().equals(GrantType.JWT_BEARER) ||
                grant.getType().equals(JWT_BEARER_PWD)) {

            at = emitAccessTokenFromJWTWith(clientInfo, (JWTBearerGrant) grant);
            //idToken = emitIDTokenFromJWT(clientInfo, (JWTBearerGrant) grant);

            if (logger.isTraceEnabled())
                logger.trace("Validating JWT Bearer Grant [" + expectedClientId.getValue() + "]");

            try {
                // This grant type is stateless, no authorization code is required
                JWTBearerGrant jwtBearerGrant = (JWTBearerGrant) grant;
                SignedJWT clientAssertion = (SignedJWT) jwtBearerGrant.getJWTAssertion();

                // TODO : Configure HMAC Algorithm ?!
                SecretKey secretKey = getKey(clientInfo);
                if (!clientAssertion.verify(new MACVerifier(secretKey.getEncoded())))
                    throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "invalid signature");

                ReadOnlyJWTClaimsSet claims = clientAssertion.getJWTClaimsSet();

                if (claims.getExpirationTime().getTime() < now)
                    throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "exp: error");

                if (claims.getNotBeforeTime().getTime() > now)
                    throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "nbt: error");

            } catch (java.text.ParseException e) {
                logger.error(e.getMessage(), e);
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, e.getMessage());
            }


        } else {
            logger.warn("Unsupported grant_type : " + grant.getType().getValue());
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, grant.getType().getValue());
        }

        if (logger.isDebugEnabled())
            logger.debug("Client ["+clientID.getValue()+"] grant validated: " + grant.getType().getValue());


        return clientInfo;

    }

    protected AccessToken emitAccessTokenFromJWTWith(ClientInformation clientInfo, JWTBearerGrant grant) throws OpenIDConnectProviderException {

        try {

            // Authenticate User with JWT
            JWT assertion = grant.getJWTAssertion();

            ReadOnlyJWTClaimsSet claims = assertion.getJWTClaimsSet();

            // Decrypt?!
            String username = claims.getSubject();
            String password = (String) claims.getClaim("cred");

            // TODO : Make configurable
            long lifetimeInSecs = 600L;

            // TODO : Make supported/available scopes configurable, and request based (token/authorization requests)
            // TODO : Link with attribute profiles ?!
            Scope scopes = new Scope();
            scopes.add(OIDCScopeValue.OPENID);
            scopes.add(OIDCScopeValue.EMAIL);
            scopes.add(OIDCScopeValue.PROFILE);

            BearerAccessToken token = new BearerAccessToken(64, lifetimeInSecs, scopes);

            return token;

        } catch (java.text.ParseException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "JWT error");
        }
    }

    protected JWT emitIDTokenFromJWTWith(ClientInformation clientInfo, JWTBearerGrant grant) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    protected AccessToken emitAccessTokenFromAuthzCode(ClientInformation clientInfo, AuthorizationCodeGrant grant) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    protected JWT emitIDTokenFromAuthzCode(ClientInformation clientInfo, AuthorizationCodeGrant grant) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    protected void authenticateClient(ClientInformation clientInfo, TokenRequest tokenRequest) throws OpenIDConnectProviderException {

        try {

            Secret secret = null;
            ClientID clientId = null;

            ClientAuthenticationMethod enabledAuthnMethod = clientInfo.getMetadata().getTokenEndpointAuthMethod();
            ClientAuthentication clientAuthn = tokenRequest.getClientAuthentication();

            if (enabledAuthnMethod == null) {
                if (logger.isDebugEnabled())
                    logger.debug("Client Authentication not required for " + clientInfo.getID().getValue());
                return;
            }

            if (!clientAuthn.getMethod().equals(enabledAuthnMethod))
                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());

            if (clientAuthn instanceof ClientSecretBasic) {
                ClientSecretBasic clientBasicAuthn = (ClientSecretBasic) clientAuthn;
                secret = clientBasicAuthn.getClientSecret();
                clientId = clientBasicAuthn.getClientID();

                if (clientId == null) {
                    logger.warn("No valid Client ID found for grant:" + tokenRequest.getAuthorizationGrant().getType().getValue() +
                            ". [" + clientAuthn.getMethod().getValue() + "]");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
                }

                if (secret == null) {
                    logger.warn("No valid Secret found for grant:" + tokenRequest.getAuthorizationGrant().getType().getValue() +
                            ". [" + clientAuthn.getMethod().getValue() + "]");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
                }

                if (!clientId.equals(clientInfo.getID()) ||
                        !secret.equals(clientInfo.getSecret())) {
                    if (logger.isDebugEnabled())
                        logger.debug("Invalid Client ID/Secret received");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
                }



            } else if (clientAuthn instanceof ClientSecretPost) {
                ClientSecretPost clientPostAuthn = (ClientSecretPost) clientAuthn;
                secret = clientPostAuthn.getClientSecret();
                clientId = clientPostAuthn.getClientID();

                if (clientId == null) {
                    logger.warn("No valid Client ID found for grant:" + tokenRequest.getAuthorizationGrant().getType().getValue() +
                            ". [" + clientAuthn.getMethod().getValue() + "]");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
                }

                if (secret == null) {
                    logger.warn("No valid Secret found for grant:" + tokenRequest.getAuthorizationGrant().getType().getValue() +
                            ". [" + clientAuthn.getMethod().getValue() + "]");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
                }

                if (!clientId.equals(clientInfo.getID()) ||
                        !secret.equals(clientInfo.getSecret())) {
                    if (logger.isDebugEnabled())
                        logger.debug("Invalid Client ID/Secret received");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
                }

            } else if (clientAuthn instanceof ClientSecretJWT) {
                ClientSecretJWT clientJWTAuthn = (ClientSecretJWT) clientAuthn;
                clientId = clientJWTAuthn.getClientID();

                // Verify signature w/secret
                JWSVerifier verifier = new MACVerifier(clientInfo.getSecret().getValueBytes());
                SignedJWT assertion = clientJWTAuthn.getClientAssertion();
                if (!assertion.verify(verifier)) {
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue() + " : invalid signature");
                }

                // TODO : Verify other JWT Token attributes
                Audience aud = clientJWTAuthn.getJWTAuthenticationClaimsSet().getAudience();
                Date exp = clientJWTAuthn.getJWTAuthenticationClaimsSet().getExpirationTime();
                Date iat = clientJWTAuthn.getJWTAuthenticationClaimsSet().getIssueTime();
                JWTID jit = clientJWTAuthn.getJWTAuthenticationClaimsSet().getJWTID();


            } else if (clientAuthn instanceof PrivateKeyJWT) {
                // TODO : Implement this
                logger.warn("No valid ClientAuthentication information found for grant:" + tokenRequest.getAuthorizationGrant().getType().getValue() +
                        ". [" + clientAuthn.getMethod().getValue() + "]");
                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
            } else {
                logger.warn("No valid ClientAuthentication information found for grant:" + tokenRequest.getAuthorizationGrant().getType().getValue() +
                        ". [" + clientAuthn.getMethod().getValue() + "]");
                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
            }

        } catch (JOSEException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_CLIENT, "client authentication error");
        }

    }

    protected SecretKey getKey(ClientInformation clientInfo) throws NoSuchAlgorithmException {

        // TODO : Is this standard procedure ?!
        byte[] key = clientInfo.getSecret().getValueBytes();
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 32);

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        return secretKey;
    }

    /**
     * Works for both binding and identity providers.
     * Useful when using TokenProducer on IdPs or Binding providers (OIDC)
     */
    protected OIDCClientInformation resolveClient(ClientID clientID) {

        // For Identity Providers
        FederationChannel fChannel = (FederationChannel) channel;
        if (fChannel instanceof SPChannel) {
            if (logger.isTraceEnabled())
                logger.trace("Getting Client Information from current SP channel " + fChannel.getName());

            for (FederatedProvider p : fChannel.getTrustedProviders()) {
                // We are looking for a SAML 2 SP Proxy for the Relaying Party.
                if (p instanceof ServiceProvider) {
                    ServiceProvider sp = (ServiceProvider) p;

                    String bpRole = sp.getBindingChannel().getFederatedProvider().getRole();

                    if (bpRole.equals(OpenIDConnectConstants.IDPSSODescriptor_QNAME.toString())) {
                        // This is a relaying party facing channel.
                        BindingChannel bc = sp.getBindingChannel();

                        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) bc.getIdentityMediator();
                        OIDCClientInformation client = mediator.getClient();
                        if (client.getID().equals(clientID))
                            return client;

                    }
                }
            }

        } else if (fChannel instanceof BindingChannel) {
            if (logger.isTraceEnabled())
                logger.trace("Getting Client Information from current binding channel " + fChannel.getName());
            BindingChannel bc = (BindingChannel) fChannel;
            OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) bc.getIdentityMediator();
            OIDCClientInformation client = mediator.getClient();
            if (client.getID().equals(clientID))
                return client;
        }

        logger.warn("Client ID not found " + clientID.getValue());

        return null;

    }

    protected TokenErrorResponse buildErrorResponse(String code , String description) {
        return buildErrorResponse(new ErrorObject(code, description, HTTPResponse.SC_BAD_REQUEST));
    }


    protected TokenErrorResponse buildErrorResponse(String code , String description, int httpStatusCode) {
        return buildErrorResponse(new ErrorObject(code, description, httpStatusCode));
    }

    protected TokenErrorResponse buildErrorResponse(ErrorObject error) {
        TokenErrorResponse r = new TokenErrorResponse(error);

        return r;
    }

    protected TokenResponse buildAccessTokenResponse() {
        return null;
    }

    protected void validateRequest(TokenRequest tokenRequest, OpenIDConnectAuthnContext authnCtx) throws OpenIDConnectException {
        // TODO : Validate received code w/authnCtx authz code

        // TODO : Validate grant_type

        // TODO : Validate request_uri (wiht the one requested, not configured!)

    }

}
