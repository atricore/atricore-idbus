package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectOPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectTokenType;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Producer that receives an assertion
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

        OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx");

        AuthenticationRequest  authnRequest = authnCtx.getAuthnRequest();
        SPInitiatedAuthnRequestType request = authnCtx != null ? authnCtx.getSsoAuthnRequest() : null;
        if (request == null) {
            // TODO : Process unsolicited response
            // validateUnsolicitedAuthnResposne(exchange, response);
        } else {
            validateSolicitedAuthnResponse(exchange, request, response);
        }

        OpenIDConnectOPMediator bpMediator = (OpenIDConnectOPMediator) channel.getIdentityMediator();

        // Resolve OpenID client

        // Resolve response ED
        EndpointDescriptor ed = resolveRedirectUri(authnRequest);

        // build response
        AuthenticationResponse authnResponse = buildAuthorizationResponse(exchange, authnCtx, authnRequest);

        // Store tokens
        //authnCtx.setAuthzGrant(authnResponse.getCode());
        //authnCtx.setAccessToken(authnResponse.getAccessToken());
        //authnCtx.setIdToken(authnResponse.getIdToken());

        // TODO : Store unmarshalled tokens w/expiration

        // Clear authn context
        authnCtx.setSsoAuthnRequest(null);
        authnCtx.setAuthnRequest(null);

        out.setMessage(new MediationMessageImpl(request.getID(),
                authnResponse,
                "AuthorizationResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected EndpointDescriptor resolveRedirectUri(AuthenticationRequest authnRequest) {

        String redirectUriStr = null;
        if (authnRequest != null)
            redirectUriStr = ""; // TODO : authnRequest.getRedirectUri();

        OpenIDConnectBinding binding = OpenIDConnectBinding.OPENID_PROVIDER_AUTHZ_HTTP;

        return new EndpointDescriptorImpl("OpenIDConnectRedirectUri",
                OpenIDConnectService.AuthorizationConsumerService.toString(), binding.getValue(), redirectUriStr, null);
    }

    protected AuthenticationResponse buildAuthorizationResponse(CamelMediationExchange exchange,
                                                                   OpenIDConnectAuthnContext authnCtx,
                                                                   AuthenticationRequest authnRequest) {

        AuthenticationResponse authnResponse = null;
        // TODO : ERROR handling
        /*
        AuthenticationResponse authnResponse = new AuthenticationResponse();

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();

        // Send back original state
        authnResponse.setState(authnRequest != null ? authnRequest.getState() : null);

        // Set all requested tokens as part of the response.
        OpenIDConnectTokenType[] requestedTokens = getRequestedTokens(authnRequest);
        for (OpenIDConnectTokenType tokenType : requestedTokens) {

            String tokenValue = resolveToken(response, tokenType.getFQTN());

            if (tokenType.equals(OpenIDConnectTokenType.AUTHZ_CODE)) {
                authnResponse.setCode(tokenValue);
            } else if (tokenType.equals(OpenIDConnectTokenType.ACCESS_TOKEN)) {
                authnResponse.setAccessToken(tokenValue);
                authnResponse.setTokenType("bearer");
            } else if (tokenType.equals(OpenIDConnectTokenType.ID_TOKEN)) {
                //
                authnResponse.setIdToken(tokenValue);
                authnResponse.setExpiresIn(3600L); // TODO : Take from ID Token
            }
        }*/

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
