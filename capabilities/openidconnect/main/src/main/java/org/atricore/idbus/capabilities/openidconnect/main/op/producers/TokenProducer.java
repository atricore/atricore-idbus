package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.Util;
import org.atricore.idbus.capabilities.openidconnect.main.op.*;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolverException;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.util.IdRegistry;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.w3._1999.xhtml.Code;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.atricore.idbus.capabilities.sts.main.WSTConstants.WST_OIDC_ID_TOKEN_TYPE;

/**
 * This creates an OIDC token based on different grant types (CODE, JWT BEARER, etc).
 *
 * The producer actually runs as part of the SSO/SAML Producers because it access the STS.  Since JOSSO
 * is SAML enabled, the STS will issue the token based on a previous authentication that created a SAML assertion
 * and an AUTHZ CODE as part of the SAML statements.
 */
public class TokenProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    // Ten seconds (TODO : Get from mediator/console)
    private long timeToleranceInMillis = 5L * 60L * 1000L;

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

        // If we have an authorization_code , the state MUST have the proper alternate key.
        AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();

        // Validate the incoming request
        OIDCClientInformation clientInfo = validateRequest(exchange, tokenRequest);

        FederationChannel fChannel = (FederationChannel) channel;

        // ----------------------------------------------
        // Emit tokens from Grant
        // ----------------------------------------------
        ClientID clientID = clientInfo.getID();
        long now = System.currentTimeMillis();
        AccessToken at = null;
        RefreshToken rt = null;
        JWT idToken = null;
        String previousIdTokenStr = (String) state.getLocalVariable(WST_OIDC_ID_TOKEN_TYPE);
        String idTokenStr = null;

        // Prepare emission context
        OpenIDConnectSecurityTokenEmissionContext ctx = new OpenIDConnectSecurityTokenEmissionContext();
        ctx.setIssuer(fChannel.getMember().getAlias());

        // We should have one if the user was authenticated using the UI, and the authz code is correct.
        if (previousIdTokenStr != null) {
            ctx.setPreviousIdToken(previousIdTokenStr); // Send ID Token as reference
        }

        // Make Grant specific validations
        // ----------------------------------------------
        if (grant.getType().equals(GrantType.AUTHORIZATION_CODE)) {

            at = (AccessToken) emitTokenFromAuthzCode(state, clientInfo, (AuthorizationCodeGrant) grant, WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE, ctx);
            rt = (RefreshToken) emitTokenFromAuthzCode(state, clientInfo, (AuthorizationCodeGrant) grant, WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE, ctx);
            idTokenStr = ctx.getIDToken();

            // Add refresh token as alternative state ID
            if (rt != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_REFRESH_TOKEN_KEY, rt.getValue());
            if (at != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, at.getValue());

        } else if (grant.getType().equals(GrantType.JWT_BEARER) ||
                grant.getType().equals(JWT_BEARER_PWD)) {

            at = (AccessToken) emitTokenForJWTBearer(state, clientInfo, (JWTBearerGrant) grant, WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE, ctx);
            rt = (RefreshToken) emitTokenForJWTBearer(state, clientInfo, (JWTBearerGrant) grant, WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE, ctx);
            idTokenStr = ctx.getIDToken();

            // Add refresh token as alternative state ID
            if (rt != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_REFRESH_TOKEN_KEY, rt.getValue());
            if (at != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, at.getValue());


        } else if (grant.getType().equals(GrantType.REFRESH_TOKEN)) {

            at = (AccessToken) emitTokenFromRefreshToken(state, clientInfo, (RefreshTokenGrant) grant, WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE, ctx);
            rt = (RefreshToken) emitTokenFromRefreshToken(state, clientInfo, (RefreshTokenGrant) grant, WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE, ctx);
            idTokenStr = ctx.getIDToken();

            // Add refresh token as alternative state ID
            if (rt != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_REFRESH_TOKEN_KEY, rt.getValue());
            if (at != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, at.getValue());


        } else {
            logger.warn("Unsupported grant_type : " + grant.getType().getValue());
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, grant.getType().getValue());
        }

        idToken = JWTParser.parse(idTokenStr);

        OIDCTokens tokens  = new OIDCTokens(idToken, at, rt);
        TokenResponse tokenResponse = buildAccessTokenResponse(clientInfo, tokens);

        ctx.setAccessToken(at);
        ctx.setRefreshToken(rt);
        ctx.setIDToken(idTokenStr);

        // Store tokens in state
        OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext) state.getLocalVariable(AUTHN_CTX_KEY);
        if (authnCtx == null)
            authnCtx = new OpenIDConnectAuthnContext();

        authnCtx.setIdTokenStr(idTokenStr);
        authnCtx.setRefreshToken(rt);
        authnCtx.setAccessToken(at);

        state.setLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY, authnCtx);
        state.setLocalVariable(WST_OIDC_ID_TOKEN_TYPE, idTokenStr);

        // Send response back (this is a back-channel request)
        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                tokenResponse,
                "AccessTokenResponse",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);


    }

    /**
     * Emits an access or refresh token
     */
    protected Token emitTokenWithBasicAuthn(
            ClientInformation clientInfo,
            OpenIDConnectSecurityTokenEmissionContext ctx,
            String tokenType,
            String username,
            String password) throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emit a new security token
        // -------------------------------------------------------

        // Queue this context and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(ctx);
        SecurityTokenService sts = ((SPChannel) channel).getSecurityTokenService();

        // Access Token
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(clientInfo, username, password, tokenType, emitterCtxArtifact.getContent());

        if (logger.isDebugEnabled())
            logger.debug("Requesting OIDC Access Token (RST) w/context " + rst.getContext());

        // Send request to STS
        try {
            RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);

            if (logger.isDebugEnabled())
                logger.debug("Received Request Security Token Response (RSTR) w/context " + rstrt.getContext());

            // Obtain access token from STS Response
            JAXBElement<RequestedSecurityTokenType> jaxbElementToken = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);
            Token token = (Token) jaxbElementToken .getValue().getAny();
            if (logger.isDebugEnabled())
                logger.debug("Generated Token [" + token.getValue() + "]");

            return token;

        } catch (SecurityTokenAuthenticationFailure e) {
            logger.error(e.getMessage());
            throw new OpenIDConnectProviderException(OAuth2Error.ACCESS_DENIED, "authn_failure");
        }

    }


    protected Token emitTokenFromRefreshToken(MediationState state, ClientInformation clientInfo, RefreshTokenGrant grant,
                                              String tokenType,
                                              OpenIDConnectSecurityTokenEmissionContext ctx)
            throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emit a new security token
        // -------------------------------------------------------

        // Queue this context and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(ctx);

        SecurityTokenService sts = ((SPChannel) channel).getSecurityTokenService();
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(clientInfo, grant, tokenType, emitterCtxArtifact.getContent());

        if (logger.isDebugEnabled())
            logger.debug("Requesting OpenID Connect 2 Access Token (RST) w/context " + rst.getContext());

        // Send request to STS
        try {
            RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);

            if (logger.isDebugEnabled())
                logger.debug("Received Request Security Token Response (RSTR) w/context " + rstrt.getContext());

            // Recover emission context, to retrieve Subject information
            ctx = (OpenIDConnectSecurityTokenEmissionContext) aqm.pullMessage(ArtifactImpl.newInstance(rstrt.getContext()));

            // Obtain access token from STS Response
            JAXBElement<RequestedSecurityTokenType> jaxbElementToken = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);
            Token token = (Token) jaxbElementToken.getValue().getAny();
            if (logger.isDebugEnabled())
                logger.debug("Generated OIDC Access Token [" + token.getValue() + "]");

            return token;

        } catch (SecurityTokenAuthenticationFailure e) {
            logger.error(e.getMessage());
            throw new OpenIDConnectProviderException(OAuth2Error.ACCESS_DENIED, "authn_failure");
        }

    }

    /**
     * For now this only works for JWT Bearer PWD grant (IdBus extension)
     *
     * JWT MUST be signed and encrypted.
     */
    protected Token emitTokenForJWTBearer(MediationState state, ClientInformation clientInfo, JWTBearerGrant grant,
                                          String tokenType,
                                          OpenIDConnectSecurityTokenEmissionContext ctx)
            throws Exception {

        try {

            if (logger.isTraceEnabled())
                logger.trace("Validating JWT Bearer Grant [" + clientInfo.getID().getValue() + "]");

            long now = System.currentTimeMillis();

            // Authenticate User with JWT
            JWT assertion = grant.getJWTAssertion();
            if (!(assertion instanceof EncryptedJWT)) {
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "not-encrypted");
            }

            OIDCClientMetadata md = (OIDCClientMetadata) clientInfo.getMetadata();

            OpenIDConnectOPMediator mediator = (OpenIDConnectOPMediator) channel.getIdentityMediator();



            JWTClaimsSet claims = null;
            if (md.getRequestObjectJWEEnc() != null) {

                // Decrypt JWT Token
                EncryptedJWT encryptedAssertion = (EncryptedJWT) assertion;
                JWEDecrypter decrypter = getDecrypter(mediator, clientInfo);
                if (decrypter == null)
                    throw new OpenIDConnectProviderException(OAuth2Error.SERVER_ERROR, "invalid JWE Encryption setup");
                encryptedAssertion.decrypt(decrypter);

                // This must be a nested JWS
                Payload payload = encryptedAssertion.getPayload();
                SignedJWT signedAssertion = payload.toSignedJWT();

                // Verify signature
                JWSVerifier verifier = getVerifier(mediator, clientInfo);
                signedAssertion.verify(verifier);

                // TODO : Verify signature of nested JWT ?
                claims = signedAssertion.getJWTClaimsSet();
            } else {
                // TODO : Better error handling
                logger.error("No claims received/processed ...");
            }

            // Unique JWT ID
            String jit = claims.getJWTID();
            IdRegistry idRegistry = mediator.getIdRegistry();
            if (jit == null || idRegistry.isUsed(jit)) {
                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, grant.getType() + " : invalid jti");
            }

            // Get user credentials (only jwt-bearer-pwd supported for now).
            String username = claims.getSubject();
            if (username == null)
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "no subject");

            String password = (String) claims.getClaim("cred");
            if (password == null)
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "no password");

            if (claims.getExpirationTime() != null && claims.getExpirationTime().getTime() < now - getTimeToleranceInMillis())
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "exp: error");

            if (claims.getNotBeforeTime() != null && claims.getNotBeforeTime().getTime() > now + getTimeToleranceInMillis())
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "nbt: error");

            // Actually emit the tokens
            return emitTokenWithBasicAuthn(clientInfo, ctx, tokenType, username, password);

        } catch (OpenIDConnectProviderException e) {
            throw e;
        } catch (java.text.ParseException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "JWT error");
        } catch (JOSEException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "JWT error");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "JWT error");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "JWT error");
        }
    }

    protected Token emitTokenFromAuthzCode(MediationState state,
                                           ClientInformation clientInfo,
                                           AuthorizationCodeGrant authzGrant,
                                           String tokenType,
                                           OpenIDConnectSecurityTokenEmissionContext ctx)
            throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emit a new security token
        // -------------------------------------------------------

        // Queue this context and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(ctx);

        SecurityTokenService sts = ((SPChannel) channel).getSecurityTokenService();
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(clientInfo, authzGrant, tokenType, emitterCtxArtifact.getContent());

        if (logger.isDebugEnabled())
            logger.debug("Requesting OpenID Connect 2 Access Token (RST) w/context " + rst.getContext());

        // Send request to STS
        try {
            RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);

            if (logger.isDebugEnabled())
                logger.debug("Received Request Security Token Response (RSTR) w/context " + rstrt.getContext());

            // Recover emission context, to retrieve Subject information
            ctx = (OpenIDConnectSecurityTokenEmissionContext) aqm.pullMessage(ArtifactImpl.newInstance(rstrt.getContext()));

            // Obtain access token from STS Response
            JAXBElement<RequestedSecurityTokenType> jaxbElementToken = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);
            Token token = (Token) jaxbElementToken.getValue().getAny();
            if (logger.isDebugEnabled())
                logger.debug("Generated OIDC Access Token [" + token.getValue() + "]");

            return token;

        } catch (SecurityTokenAuthenticationFailure e) {
            logger.error(e.getMessage());
            throw new OpenIDConnectProviderException(OAuth2Error.ACCESS_DENIED, "authn_failure");
        }

    }

    protected RequestSecurityTokenType buildRequestSecurityToken(ClientInformation client,
                                                               String username,
                                                               String password,
                                                               String tokenType,
                                                               String context) throws OpenIDConnectException {
        logger.debug("generating RequestSecurityToken...");
        org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

        RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

        rstRequest.getAny().add(of.createTokenType(tokenType));
        rstRequest.getAny().add(of.createRequestType(WSTConstants.WST_ISSUE_REQUEST));

        org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

        // Send credentials with authn request:
        UsernameTokenType usernameToken = new UsernameTokenType ();
        org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString usernameString = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString();
        usernameString.setValue(username);

        usernameToken.setUsername(usernameString);
        usernameToken.getOtherAttributes().put(new QName(Constants.PASSWORD_NS), password);
        usernameToken.getOtherAttributes().put(CLIENT_ID, client.getID().getValue());

        rstRequest.getAny().add(ofwss.createUsernameToken(usernameToken));

        if (context != null)
            rstRequest.setContext(context);

        logger.debug("generated RequestSecurityToken [" + rstRequest + "]");
        return rstRequest;
    }

    protected RequestSecurityTokenType buildRequestSecurityToken(ClientInformation client,
                                                                 AuthorizationCodeGrant authzGrant,
                                                                 String tokenType,
                                                                 String context) throws OpenIDConnectException {


        logger.debug("generating RequestSecurityToken...");
        try {
            org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

            RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

            rstRequest.getAny().add(of.createTokenType(tokenType));
            rstRequest.getAny().add(of.createRequestType(WSTConstants.WST_ISSUE_REQUEST));

            org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

            // Send credentials with authn request:
            BinarySecurityTokenType authzGrantToken = new BinarySecurityTokenType();

            authzGrantToken.setValue(Util.marshallMultiValue(authzGrant.toParameters()));
            authzGrantToken.setValueType(authzGrant.getType().getValue());
            authzGrantToken.setEncodingType("json");

            authzGrantToken.getOtherAttributes().put(new QName(Constants.AUTHZ_CODE_NS), client.getID().getValue());
            authzGrantToken.getOtherAttributes().put(CLIENT_ID, client.getID().getValue());

            rstRequest.getAny().add(ofwss.createBinarySecurityToken(authzGrantToken));

            if (context != null)
                rstRequest.setContext(context);

            logger.debug("generated RequestSecurityToken [" + rstRequest + "]");
            return rstRequest;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectException(e);
        }
    }

    protected RequestSecurityTokenType buildRequestSecurityToken(ClientInformation client,
                                                                 RefreshTokenGrant refreshTokenGrant,
                                                                 String tokenType,
                                                                 String context) throws OpenIDConnectException {


        logger.debug("generating RequestSecurityToken...");
        try {
            org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

            RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

            rstRequest.getAny().add(of.createTokenType(tokenType));
            rstRequest.getAny().add(of.createRequestType(WSTConstants.WST_ISSUE_REQUEST));

            org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

            // Send credentials with authn request:
            BinarySecurityTokenType authzGrantToken = new BinarySecurityTokenType();

            authzGrantToken.setValue(Util.marshallMultiValue(refreshTokenGrant.toParameters()));
            authzGrantToken.setValueType(refreshTokenGrant.getType().getValue());
            authzGrantToken.setEncodingType("json");

            authzGrantToken.getOtherAttributes().put(new QName(Constants.REFRESH_TOKEN_NS), client.getID().getValue());
            authzGrantToken.getOtherAttributes().put(CLIENT_ID, client.getID().getValue());

            rstRequest.getAny().add(ofwss.createBinarySecurityToken(authzGrantToken));

            if (context != null)
                rstRequest.setContext(context);

            logger.debug("generated RequestSecurityToken [" + rstRequest + "]");
            return rstRequest;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectException(e);
        }
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

        try {
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

            OIDCClientInformation clientInfo = resolveClient(clientID);
            if (clientInfo == null) {
                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "client_id:" + clientID.getValue());
            }

            if (logger.isDebugEnabled())
                logger.debug("Processing TokenRequest for " + clientID.getValue());
            ClientID expectedClientId = clientInfo.getID();

            // ----------------------------------------------
            // Authenticate Client
            // ----------------------------------------------
            authenticateClient(state, clientInfo, tokenRequest);

            // ----------------------------------------------
            // Verify that the Client support the Authorization Grant
            // ----------------------------------------------
            AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();
            if (grant == null)
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "grant_type:n/a");
            if (!clientInfo.getOIDCMetadata().getGrantTypes().contains(grant.getType())) {
                if (logger.isDebugEnabled())
                    logger.debug("Invalid Authorization Grant [" + tokenRequest.getAuthorizationGrant() + "] for Client " + expectedClientId.getValue());

                throw new OpenIDConnectProviderException(OAuth2Error.UNSUPPORTED_GRANT_TYPE, "grant_type:" + grant.getType().getValue());
            }


            return clientInfo;
        } finally {
            // Code cannot be used again!
            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            MediationState state = in.getMessage().getState();
            state.getLocalState().removeAlternativeId("code");
            state.removeLocalVariable(OIDC_EXT_NAMESPACE + ":code_challenge");
            state.removeLocalVariable(OIDC_EXT_NAMESPACE + ":code_challenge_method");
        }

    }

    protected void authenticateClient(MediationState state, ClientInformation clientInfo, TokenRequest tokenRequest) throws OpenIDConnectProviderException {

        try {

            Secret secret = null;
            ClientID clientId = null;
            long now = System.currentTimeMillis();

            ClientAuthenticationMethod enabledAuthnMethod = clientInfo.getMetadata().getTokenEndpointAuthMethod();
            ClientAuthentication clientAuthn = tokenRequest.getClientAuthentication();
            AuthorizationGrant authzGrant = tokenRequest.getAuthorizationGrant();
            CodeVerifier cv = null;

            // No authn method means code challenge!
            if (authzGrant instanceof AuthorizationCodeGrant && enabledAuthnMethod == null) {

                AuthorizationCodeGrant authzCodeGrant = (AuthorizationCodeGrant) authzGrant;
                cv = authzCodeGrant.getCodeVerifier();
                if (cv == null) {
                    logger.trace("No code_verifier received, and client authentication method is set to NONE");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "code_verifier");
                }

                if (logger.isTraceEnabled())
                    logger.trace("Received code_verifier " + cv.getValue());

                String expectedCodeChallengeStr = (String) state.getLocalVariable(OIDC_EXT_NAMESPACE + ":code_challenge");

                CodeChallengeMethod codeChallengeMethod = CodeChallengeMethod.getDefault();
                if (expectedCodeChallengeStr == null) {
                    logger.trace("No code_verifier received, and client authentication method is set to NONE");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "code_challenge_not_found");
                }

                CodeChallenge expectedCodeChallenge = null;
                try {
                    expectedCodeChallenge = CodeChallenge.parse(expectedCodeChallengeStr);
                } catch (ParseException e) {
                    logger.error("Invalid code challenge " + expectedCodeChallengeStr);
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "invalid_code_challenge");
                }

                if (state.getLocalVariable(OIDC_EXT_NAMESPACE + ":code_challenge_method") != null) {
                    codeChallengeMethod = CodeChallengeMethod.parse((String) state.getLocalVariable(OIDC_EXT_NAMESPACE + ":code_challenge_method"));
                }

                if (logger.isTraceEnabled())
                    logger.trace("Attempting code_challenge authentication ["+codeChallengeMethod.getValue()+"] " + codeChallengeMethod.getValue());

                CodeChallenge codeChallenge = CodeChallenge.compute(codeChallengeMethod, cv);
                if (codeChallenge.equals(expectedCodeChallenge)) {
                    if (logger.isTraceEnabled())
                        logger.trace("Authenticated client with code_challenge " + expectedCodeChallenge.getValue());
                    return;
                }

                if (logger.isTraceEnabled())
                    logger.trace("Failed client authentication, expected/received code_challenge " + expectedCodeChallenge.getValue() + "/" + codeChallenge.getValue());

                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "code_verifier");

            } else if (authzGrant instanceof RefreshTokenGrant) {
                // This is a token refresh request

                // Validate the received refresh token
                RefreshToken rt = tokenRequest.getExistingGrant();

                if (rt == null || rt.getValue() == null) {
                    if (logger.isTraceEnabled())
                        logger.trace("Failed client authentication, previous refresh_token ");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "no_refresh_token");
                }

                OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

                if (authnCtx == null || authnCtx.getRefreshToken() == null) {
                    if (logger.isTraceEnabled())
                        logger.trace("Failed client authentication, previous refresh_token ");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "no_refresh_token");
                }

                if (!rt.getValue().equals(authnCtx.getRefreshToken().getValue())) {
                    if (logger.isTraceEnabled())
                        logger.trace("Failed client authentication, previous refresh_token ");
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "invalid_refresh_token");
                }

            }

            // No authentication method MUST use CODE CHALLENGE/VERIFIER or REFRESH_TOKEN grant!
            if (enabledAuthnMethod == null) {
                if (logger.isDebugEnabled())
                    logger.debug("Client Authentication not required for " + clientInfo.getID().getValue());
                return;
            }

            if (clientAuthn.getMethod() == null || !clientAuthn.getMethod().equals(enabledAuthnMethod)) {
                logger.error("The authentication method used by the client is not enabled: " + clientAuthn.getMethod());
                throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue());
            }

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
                JWSVerifier verifier = new MACVerifier(KeyUtils.extendOrTruncateKey(clientInfo).getEncoded());
                SignedJWT assertion = clientJWTAuthn.getClientAssertion();
                if (!assertion.verify(verifier)) {
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue() + " : invalid_signature");
                }

                // Audience
                FederationChannel fChannel = (FederationChannel) channel;

                List<Audience> auds = clientJWTAuthn.getJWTAuthenticationClaimsSet().getAudience();

                String expectedAudience = fChannel.getMember().getAlias();

                boolean found = false;
                for (Audience aud : auds) {
                    if (aud.getValue().equals(expectedAudience)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    if (logger.isDebugEnabled())
                        logger.debug("aud expected : " + expectedAudience);
                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "invalid_audience ");
                }

                // Expiration date, it can be up to five minutes in the past
                Date exp = clientJWTAuthn.getJWTAuthenticationClaimsSet().getExpirationTime();
                if (exp == null || exp.getTime() < now - getTimeToleranceInMillis()) {
                    if (logger.isDebugEnabled())
                        logger.debug("exp (received/now) : " + exp + " < " + new Date(now + getTimeToleranceInMillis()));

                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue() + " : expired_JWT");
                }

                // Issue At time, it can be up to five minutes in the future.
                Date iat = clientJWTAuthn.getJWTAuthenticationClaimsSet().getIssueTime();
                if (iat == null || iat.getTime() > now + getTimeToleranceInMillis()) {
                    if (logger.isDebugEnabled())
                        logger.debug("iat (received/now) : " + iat + " > " + new Date(now + getTimeToleranceInMillis()));

                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue() + " : invalid_iat");
                }

                // Unique JWT ID
                JWTID jti = clientJWTAuthn.getJWTAuthenticationClaimsSet().getJWTID();
                OpenIDConnectOPMediator mediator = (OpenIDConnectOPMediator) channel.getIdentityMediator();
                IdRegistry idRegistry = mediator.getIdRegistry();
                if (jti == null || idRegistry.isUsed(jti.getValue())) {
                    if (logger.isDebugEnabled())
                        logger.debug("jti (reused) : " + jti);

                    throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, clientAuthn.getMethod().getValue() + " : invalid_jti");
                }

                idRegistry.register(jti.getValue(), 60 * 60); // Mark the JIT used for an hour.

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
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_CLIENT, "client authentication error");
        }

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

            OpenIDConnectOPMediator mediator = (OpenIDConnectOPMediator) fChannel.getIdentityMediator();
            for (OIDCClientInformation client : mediator.getClients()) {
                if (client.getID().equals(clientID))
                    return client;
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

    protected TokenResponse buildAccessTokenResponse(OIDCClientInformation clientInfo, OIDCTokens tokens) {
        return new OIDCTokenResponse(tokens);
    }

    /**
     * Only works for OpenID Provider channels, using OpenIDConnectOPMediator mediator instances.
     * @return
     */
    protected MessageQueueManager getArtifactQueueManager() {
        OpenIDConnectOPMediator mediator = (OpenIDConnectOPMediator ) channel.getIdentityMediator();
        return mediator.getArtifactQueueManager();
    }

    public long getTimeToleranceInMillis() {
        return timeToleranceInMillis;
    }


    protected JWEDecrypter getDecrypter(OpenIDConnectOPMediator mediator, ClientInformation clientInfo)
            throws SSOKeyResolverException, NoSuchAlgorithmException, JOSEException {

        OIDCClientMetadata md = (OIDCClientMetadata) clientInfo.getMetadata();

        if (isEncryptionMethodSupported(DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS, md.getRequestObjectJWEEnc()) &&
            isEncryptionAlgorithmSupported(DirectDecrypter.SUPPORTED_ALGORITHMS, md.getRequestObjectJWEAlg())) {
                if (logger.isDebugEnabled())
                    logger.debug("Using Direct Decrypter, Shared Secret");
                return new DirectDecrypter(KeyUtils.extendOrTruncateKey(clientInfo));
        }

        if (isEncryptionMethodSupported(RSADecrypter.SUPPORTED_ENCRYPTION_METHODS, md.getRequestObjectJWEEnc()) &&
            isEncryptionAlgorithmSupported(RSADecrypter.SUPPORTED_ALGORITHMS, md.getRequestObjectJWEAlg())) {
            RSAPrivateKey pkey = (RSAPrivateKey) mediator.getEncryptKeyResolver().getPrivateKey();
            if (logger.isDebugEnabled())
                logger.debug("Using RSA Decrypter, Private Key " + pkey.getFormat());
            return new RSADecrypter(pkey);
        }

        if (isEncryptionMethodSupported(AESDecrypter.SUPPORTED_ENCRYPTION_METHODS, md.getRequestObjectJWEEnc()) &&
                isEncryptionAlgorithmSupported(AESDecrypter.SUPPORTED_ALGORITHMS, md.getRequestObjectJWEAlg())) {
            if (logger.isDebugEnabled())
                logger.debug("Using AES Decrypter, Shared Secret");
            return new AESDecrypter(KeyUtils.extendOrTruncateKey(clientInfo));
        }

        logger.warn("No JWE Decrypter created for " + md.getRequestObjectJWEEnc().getName() + "/" + md.getRequestObjectJWEAlg().getName());
        return null;
    }

    protected JWSVerifier getVerifier(OpenIDConnectOPMediator mediator, ClientInformation clientInfo)
            throws NoSuchAlgorithmException, JOSEException {

        OIDCClientMetadata md = (OIDCClientMetadata) clientInfo.getMetadata();
        if (isSigngingAlgorithmSupported(MACVerifier.SUPPORTED_ALGORITHMS, md.getRequestObjectJWSAlg())) {
            if (logger.isDebugEnabled())
                logger.debug("Using MAC Verifier, Shared Secret");
            return new MACVerifier(KeyUtils.extendOrTruncateKey(clientInfo).getEncoded());
        }

        if (isSigngingAlgorithmSupported(RSASSAVerifier.SUPPORTED_ALGORITHMS, md.getRequestObjectJWSAlg())) {
            throw new IllegalArgumentException("RSA Singature not supported");
            // TODO : return new RSASSAVerifier(/*public key*/)
        }

        if (isSigngingAlgorithmSupported(ECDSAVerifier.SUPPORTED_ALGORITHMS, md.getRequestObjectJWSAlg())) {
            throw new IllegalArgumentException("RSA Singature not supported");
            // TODO : return new ECDSAVerifier(/*??*/)
        }

        logger.warn("No JWS Verifier created for " + md.getRequestObjectJWSAlg());
        return null;

    }

    protected boolean isSigngingAlgorithmSupported(Set<JWSAlgorithm> supportedALgorithms, JWSAlgorithm jwsAlg) {
        if (supportedALgorithms == null)
            return false;

        for (JWSAlgorithm sa : supportedALgorithms) {
            if (sa.getName().equals(jwsAlg.getName()))
                return true;
        }

        return false;
    }

    protected boolean isEncryptionMethodSupported(Set<EncryptionMethod> supportedEncryptionMethods,
                                            EncryptionMethod jweEnc) {
        if (supportedEncryptionMethods == null) {
            return false;
        }

        for(EncryptionMethod em : supportedEncryptionMethods) {
            if (em.getName().equals(jweEnc.getName()))
                return true;
        }

        return false;
    }

    protected boolean isEncryptionAlgorithmSupported(Set<JWEAlgorithm> supportedAlgorithms,
                                                     JWEAlgorithm jweAlg) {
        if (supportedAlgorithms == null)
            return false;

        for (JWEAlgorithm ea : supportedAlgorithms) {
            if (ea.getName().equals(jweAlg.getName()))
                return true;
        }
        return false;
    }


}
