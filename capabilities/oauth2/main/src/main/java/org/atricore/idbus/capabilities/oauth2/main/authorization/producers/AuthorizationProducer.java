package org.atricore.idbus.capabilities.oauth2.main.authorization.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2IdPMediator;
import org.atricore.idbus.common.oauth._2_0.protocol.AuthorizationRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.AuthorizationResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AuthorizationProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(AuthorizationProducer.class);

    public AuthorizationProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();


        if (in.getMessage() instanceof AuthorizationRequestType) {
            AuthorizationRequestType authnReq = (AuthorizationRequestType) in.getMessage().getContent();
            doProcessAuthorizationRequest(exchange, authnReq);
        } else if (in.getMessage() instanceof AuthorizationResponseType ) {
            SPAuthnResponseType spAuthnResp = (SPAuthnResponseType) in.getMessage().getContent();
            doProcessSPAuthnResponse(exchange, spAuthnResp);
        }


    }

    protected void doProcessAuthorizationRequest(CamelMediationExchange exchange, AuthorizationRequestType authnRequest) throws Exception {

        // TODO : Trigger SSO Process by sending an authn request to SP-Binding endpoint.
        // TODO : We need to diferenciated AUTHZ-ACS from SSO-ACS .... ?!

        SPInitiatedAuthnRequestType spAuthnReq = null;

        throw new UnsupportedOperationException("Not Implemented !!!");
    }

    protected void doProcessSPAuthnResponse(CamelMediationExchange exchange, SPAuthnResponseType spAuthnResp) {

        // TODO : The received subject (spAuthnReq) will contain an OAuth2 AUTHORIZATION token (not an access token) as part of the subject attributes.
        // TODO : Later, the TokenProducer can emit an ACCESS TOKEN using the AUTHORIZATION TOKEN.

        // Send the token in the authorization response
        AuthorizationResponseType response = null;

        throw new UnsupportedOperationException("Not Implemented !!!");

    }


}
