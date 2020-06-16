package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectTokenType;
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Iterator;

/**
 * Producer that receives an assertion
 *
 * Receives an SSO Authentication Response, and issues an OpenID Authentication Response.
 */
public class AssertionConsumerProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(AssertionConsumerProducer.class);

    public AssertionConsumerProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState state = in.getMessage().getState();

        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();

        // We have an SSO Authentication response

        RPAuthnContext authnCtx = (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        if (authnCtx == null) {
            // State was lost or this is IdP initiated (not supported).
            logger.error("IdP initiated not supported (or state was lost)");
            throw new OpenIDConnectException("IdP initiated not supported (or state was lost)");
        }

        AuthenticationRequest  oidcAuthnRequest = authnCtx.getAuthnRequest();
        SPInitiatedAuthnRequestType ssoAuthnRequest = authnCtx.getSsoAuthnRequest();
        if (ssoAuthnRequest == null) {
            // TODO : Process unsolicited response
            // validateUnsolicitedAuthnResponse(exchange, response);
        } else {
            validateSolicitedAuthnResponse(exchange, ssoAuthnRequest, response);
        }


        // Build an OpenIDConnect authentication response based on the original request
        String currentIdPSession = authnCtx.getIdPSession();
        String newIdPSession = response.getSessionIndex();

        // This is a new IDP session
        if (currentIdPSession == null || !currentIdPSession.equals(newIdPSession)) {

            if (logger.isDebugEnabled())
                logger.debug("Generating new RP Session ID, previous IDP session " + (currentIdPSession != null ? currentIdPSession : "null"));

            if (logger.isDebugEnabled())
                logger.debug("Generating new RP Session ID, new IDP Session " + newIdPSession);

            String newRpSessionId = UUIDGenerator.generateJDKId();

            // Calculate session state according to specs
            // http://openid.net/specs/openid-connect-session-1_0.html#CreatingUpdatingSessions
            State newSessionState = null;
            try {
                // TODO : Verify origin
                // TODO : Use a different sessionId (local to RP)
                String salt = CipherUtil.getNextSalt(8);
                String ss = CipherUtil.createHash(
                        oidcAuthnRequest.getClientID() + " " +
                                oidcAuthnRequest.getRedirectionURI() + " " +
                                newRpSessionId + " " +
                                salt, "SHA256") +
                        "." + salt;

                newSessionState = new State(ss);

            } catch (NoSuchAlgorithmException e) {
                throw new OpenIDConnectException(e);
            }

            authnCtx.setRPSession(newRpSessionId);
            authnCtx.setRpSessionState(newSessionState);
            authnCtx.setIdPSession(newIdPSession);

            //state.setRemoteVariable(, rpSessionId);

            if (logger.isDebugEnabled())
                logger.debug("Generated new RP Session STATE " + newSessionState);


        }

        AuthenticationResponse authnResponse = buildAuthenticationResponse(exchange, authnCtx, oidcAuthnRequest, response);

        // Resolve response ED
        EndpointDescriptor ed = resolveRedirectUri(oidcAuthnRequest, (AuthorizationResponse) authnResponse);


        if (authnResponse instanceof AuthenticationSuccessResponse) {

            AuthenticationSuccessResponse sr = (AuthenticationSuccessResponse) authnResponse;
            AuthorizationCode code = sr.getAuthorizationCode();

            // Ad alternate state keys, to be used by back-channel.
            if (code != null)
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_AUTHZ_CODE_KEY, code.getValue());

            if (sr.getAccessToken() != null) {
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, sr.getAccessToken().getValue());
                authnCtx.setAccessToken(sr.getAccessToken());
            }

            // Store tokens in state
            if (sr.getIDToken() != null)
                authnCtx.setIdToken(sr.getIDToken());

            try {
                // The IDP always emits an ID token, and includes it in the assertion, regardles of the OIDC flow.
                String idTokenString = resolveToken(response, OpenIDConnectTokenType.ID_TOKEN.getFQTN());
                authnCtx.setIdTokenStr(idTokenString);
            } catch (OpenIDConnectException e) {
                logger.trace("No ID Token found in response: " + response.getID());
            }

        }

        // Clear authn context
        authnCtx.setSsoAuthnRequest(null);
        authnCtx.setAuthnRequest(null);
        authnCtx.setIdpAlias(response.getIdpAlias());

        // Store authz code
        if (authnResponse instanceof AuthenticationSuccessResponse) {
            AuthenticationSuccessResponse authnSuccessResponse = (AuthenticationSuccessResponse) authnResponse;
            AuthorizationCode code = authnSuccessResponse.getAuthorizationCode();
            authnCtx.setAuthorizationCode(code);
        }

        // Update state
        state.setLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY, authnCtx);

        out.setMessage(new MediationMessageImpl(ssoAuthnRequest.getID(),
                authnResponse,
                "AuthorizationResponse",
                null,
                ed,
                state));

        exchange.setOut(out);

    }

    protected EndpointDescriptor resolveRedirectUri(AuthenticationRequest authnRequest, AuthorizationResponse authnResponse) throws SerializeException {

        String redirectUriStr = null;
        if (authnRequest != null)
            redirectUriStr = authnResponse.toURI().toString();

        return new EndpointDescriptorImpl("OpenIDConnectRedirectUri",
                "OpenIDConnectRedirectUri",
                OpenIDConnectBinding.OPENID_PROVIDER_AUTHZ_HTTP.getValue(),
                redirectUriStr, null);
    }

    /**
     * This creates an OpenIDConnect authn response based on a received SSO/SAML Authn response
     * @param exchange
     * @param authnCtx
     * @param authnRequest
     *
     * @param response
     * @return
     */
    protected AuthenticationResponse buildAuthenticationResponse(CamelMediationExchange exchange,
                                                                 RPAuthnContext authnCtx,
                                                                 AuthenticationRequest authnRequest,
                                                                 SPAuthnResponseType response) throws OpenIDConnectException, IdentityMediationException, URISyntaxException {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        // Set all requested tokens as part of the response.
        AuthorizationCode code = null;
        BearerAccessToken accessToken = null;
        JWT idToken = null;
        ResponseType responseType = authnRequest.getResponseType();

        for (Iterator<ResponseType.Value> iterator = responseType.iterator(); iterator.hasNext(); ) {
            ResponseType.Value responseTypeValue = iterator.next();

            OpenIDConnectTokenType tokenType = OpenIDConnectTokenType.asEnum(responseTypeValue.getValue());

            if (logger.isDebugEnabled())
                logger.debug("ResponseType " + tokenType + " requested.");

            // We provide the requested tokens, this is normally either 'code'  or 'id_token token'
            if (tokenType.equals(OpenIDConnectTokenType.AUTHZ_CODE)) {

                // Look for the subject attribute that matches the token type we need to issue, if any!
                code = new AuthorizationCode(resolveToken(response, tokenType.getFQTN()));

                // Add alternative state key to keep state on back-channel requests
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_AUTHZ_CODE_KEY , code.getValue());

            } else if (tokenType.equals(OpenIDConnectTokenType.ACCESS_TOKEN)) {
                // TODO : This is also defined in the Access Token Emitter, we can hard-code or let the user define this in the console instead.
                accessToken = new BearerAccessToken(resolveToken(response, tokenType.getFQTN()), 300l, new Scope(OIDCScopeValue.OPENID));

            } else if (tokenType.equals(OpenIDConnectTokenType.ID_TOKEN)) {

                try {
                    idToken = JWTParser.parse(resolveToken(response, tokenType.getFQTN()));
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                    throw new OpenIDConnectException(e);
                }

            } else  {
                /// What token is this ?!
                logger.warn("Unknown token type " + tokenType);

            }

        }

        AuthenticationResponse authnResponse = new AuthenticationSuccessResponse(authnRequest.getRedirectionURI(),
                code,
                idToken,
                accessToken,
                authnRequest.getState(),
                authnCtx.getRPSessionState(),
                authnRequest.getResponseMode());

        return authnResponse;
    }

    protected void validateSolicitedAuthnResponse(CamelMediationExchange exchange,
                                                  SPInitiatedAuthnRequestType request, SPAuthnResponseType response)
            throws OpenIDConnectException {

        if (response == null) {
            throw new OpenIDConnectException("No response found!");
        }

        // TODO : Validate in-reply-to and other attributes: target acs, etc.
        if (response.getInReplayTo() == null || !request.getID().equals(response.getInReplayTo())) {
            throw new OpenIDConnectException("Response is not a reply to " +
                    request.getID() + " [" + (response.getInReplayTo() == null ? "<null>" : response.getInReplayTo()) + "]");
        }

        validateAuthnResponse(exchange, response);
    }

    protected void validateUnsolicitedAuthnResposne(CamelMediationExchange exchange, SPAuthnResponseType response) throws OpenIDConnectException {
        //validateAuthnResponse(exchange, response);
        if (response == null) {
            throw new OpenIDConnectException("No response found!");
        }
    }

    protected void validateAuthnResponse(CamelMediationExchange exchange, SPAuthnResponseType response) throws OpenIDConnectException {
        /*
        // Make sure that we have an OAUTH2 TOKEN
        if (resolveAccessToken(response) == null) {
            throw new OAuth2Exception("No token of type " + WSTConstants.WST_OAUTH2_TOKEN_TYPE + " received in subject attributes set");
        }
        */
    }

    /**
     * Get token from the Subject
     * @param response the SSO Autn
     * @param tokenType
     * @return
     */
    protected String resolveToken(SPAuthnResponseType response, String tokenType) throws OpenIDConnectException {

        // Get subject from response
        SubjectType subject = response.getSubject();

        // Look for a principal that matches our token type.
        for (AbstractPrincipalType p : subject.getAbstractPrincipal()) {

            if (p instanceof SubjectAttributeType) {

                SubjectAttributeType attr = (SubjectAttributeType) p;

                if (attr.getName().equals(tokenType)) {
                    return attr.getValue();
                }
            }
        }

        throw new OpenIDConnectException("No token type ["+tokenType+"] found in response " + response.getID());

    }

}
