package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectTokenType;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

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

        OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        if (authnCtx == null) {
            // State was lost or this is IdP initiated (not supported).
            logger.error("IdP initiated not supported (or state was lost)");
            throw new OpenIDConnectException("IdP initiated not supported (or state was lost)");
        }

        AuthenticationRequest  samlAuthnRequest = authnCtx.getAuthnRequest();
        SPInitiatedAuthnRequestType ssoAuthnRequest = authnCtx.getSsoAuthnRequest();
        if (ssoAuthnRequest == null) {
            // TODO : Process unsolicited response
            // validateUnsolicitedAuthnResponse(exchange, response);
        } else {
            validateSolicitedAuthnResponse(exchange, ssoAuthnRequest, response);
        }

        // Resolve OpenID client

        // Build an OpenIDConnect authentication response based on the original request
        AuthenticationResponse authnResponse = buildAuthenticationResponse(exchange, authnCtx, samlAuthnRequest);

        // Resolve response ED
        EndpointDescriptor ed = resolveRedirectUri(samlAuthnRequest, (AuthorizationResponse) authnResponse);

        // Ad alternate state key, to be used by back-channel.
        state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_AUTHZ_CODE_KEY,
                ((AuthenticationSuccessResponse) authnResponse).getAuthorizationCode().getValue());

        // TODO : Store unmarshalled tokens w/expiration

        // Clear authn context
        authnCtx.setSsoAuthnRequest(null);
        authnCtx.setAuthnRequest(null);

        // Store authz code
        if (authnResponse instanceof AuthenticationSuccessResponse) {
            AuthenticationSuccessResponse authnSuccessResponse = (AuthenticationSuccessResponse) authnResponse;
            AuthorizationCode code = authnSuccessResponse.getAuthorizationCode();
            authnCtx.setAuthorizationCode(code);
            // TODO : authnCtx.setAuthorizationCodeNotOnOrAfter(???);
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
     * This creates an OpenIDConnect authn response
     * @param exchange
     * @param authnCtx
     * @param authnRequest
     *
     * @return
     */
    protected AuthenticationResponse buildAuthenticationResponse(CamelMediationExchange exchange,
                                                                 OpenIDConnectAuthnContext authnCtx,
                                                                 AuthenticationRequest authnRequest) throws OpenIDConnectException {

        // TODO : ERROR handling
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();

        // Set all requested tokens as part of the response.
        AuthorizationCode code = null;
        BearerAccessToken accessToken = null;
        JWT idToken = null;
        ResponseType responseType = authnRequest.getResponseType();

        for (Iterator<ResponseType.Value> iterator = responseType.iterator(); iterator.hasNext(); ) {
            ResponseType.Value responseTypeValue = iterator.next();

            OpenIDConnectTokenType tokenType = OpenIDConnectTokenType.asEnum(responseTypeValue.getValue());

            // Look for the subject attribute that matches the token type we need to issue, if any!
            String tokenValue = resolveToken(response, tokenType.getFQTN());
            if (tokenValue == null)
                throw new OpenIDConnectException("No token type ["+tokenType.getFQTN()+"] found in response " + response.getID());

            if (tokenType.equals(OpenIDConnectTokenType.AUTHZ_CODE)) {
                code = new AuthorizationCode(tokenValue);

                // Add alternative state key to keep state on back-channel requests
                state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_AUTHZ_CODE_KEY , code.getValue());

            } else if (tokenType.equals(OpenIDConnectTokenType.ACCESS_TOKEN)) {
                accessToken = new BearerAccessToken(tokenValue);

            } else if (tokenType.equals(OpenIDConnectTokenType.ID_TOKEN)) {
                // TODO : Get JWT ID Token
                //idToken = tokenValue;
            }

        }

        // TODO : Calculate session state according to
        // http://openid.net/specs/openid-connect-session-1_0.html#CreatingUpdatingSessions

        AuthenticationResponse authnResponse = new AuthenticationSuccessResponse(authnRequest.getRedirectionURI(),
                code, 
                idToken, 
                accessToken, 
                authnRequest.getState(),
                null,
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
    protected String resolveToken(SPAuthnResponseType response, String tokenType) {

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

        logger.debug("No Subject attribute found ["+tokenType+"] in Subject " );

        return null;
    }

}
