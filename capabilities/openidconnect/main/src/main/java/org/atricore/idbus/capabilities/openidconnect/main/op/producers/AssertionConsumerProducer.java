package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
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

        OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:openidconnect:authnCtx");

        AuthenticationRequest  authnRequest = authnCtx.getAuthnRequest();
        SPInitiatedAuthnRequestType request = authnCtx != null ? authnCtx.getSsoAuthnRequest() : null;
        if (request == null) {
            // TODO : Process unsolicited response
            // validateUnsolicitedAuthnResposne(exchange, response);
        } else {
            validateSolicitedAuthnResponse(exchange, request, response);
        }

        // Resolve OpenID client

        // build response
        AuthenticationResponse authnResponse = buildAuthorizationResponse(exchange, authnCtx, authnRequest);

        // Resolve response ED
        EndpointDescriptor ed = resolveRedirectUri(authnRequest, (AuthorizationResponse) authnResponse);


        // Ad alternate state key, to be used by back-channel.
        state.getLocalState().addAlternativeId("authorization_code",
                ((AuthenticationSuccessResponse) authnResponse).getAuthorizationCode().getValue());

        // TODO : Store unmarshalled tokens w/expiration

        // Clear authn context
        authnCtx.setSsoAuthnRequest(null);
        authnCtx.setAuthnRequest(null);

        out.setMessage(new MediationMessageImpl(request.getID(),
                null,
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
                OpenIDConnectBinding.SSO_REDIRECT.getValue(),
                redirectUriStr, null);
    }

    protected AuthenticationResponse buildAuthorizationResponse(CamelMediationExchange exchange,
                                                                   OpenIDConnectAuthnContext authnCtx,
                                                                   AuthenticationRequest authnRequest) {

        // TODO : ERROR handling
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();

        // Set all requested tokens as part of the response.
        AuthorizationCode code = null;
        BearerAccessToken accessToken = null;
        JWT idToken = null;
        ResponseType responseType = authnRequest.getResponseType();

        for (Iterator<ResponseType.Value> iterator = responseType.iterator(); iterator.hasNext(); ) {
            ResponseType.Value responseTypeValue = iterator.next();

            OpenIDConnectTokenType tokenType = OpenIDConnectTokenType.asEnum(responseTypeValue.getValue());
            String tokenValue = resolveToken(response, tokenType.getFQTN());

            if (tokenType.equals(OpenIDConnectTokenType.AUTHZ_CODE)) {
                code = new AuthorizationCode(tokenValue);
            } else if (tokenType.equals(OpenIDConnectTokenType.ACCESS_TOKEN)) {
                accessToken = new BearerAccessToken(tokenValue);
            } else if (tokenType.equals(OpenIDConnectTokenType.ID_TOKEN)) {
                //idToken = tokenValue;
            }
        }

        AuthenticationResponse authnResponse = new AuthenticationSuccessResponse(authnRequest.getRedirectionURI(),
                code, idToken, accessToken, authnRequest.getState(), null, authnRequest.getResponseMode());

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

    protected String resolveToken(SPAuthnResponseType response, String tokenType) {
        SubjectType subject = response.getSubject();

        for (AbstractPrincipalType p : subject.getAbstractPrincipal()) {
            if (p instanceof SubjectAttributeType) {
                SubjectAttributeType attr = (SubjectAttributeType) p;

                if (attr.getName().equals(tokenType)) {
                    return attr.getValue();
                }
            }
        }

        return null;
    }

}
