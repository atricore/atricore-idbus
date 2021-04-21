package org.atricore.idbus.capabilities.oauth2.main.binding.services;

import org.atricore.idbus.common.oauth._2_0.protocol.*;
import org.atricore.idbus.common.oauth._2_0.wsdl.OAuthPortType;

import javax.jws.WebService;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@WebService(
        targetNamespace = "urn:org:atricore:idbus:common:oauth:2.0:wsdl",
        serviceName = "OAuthService",
        portName = "soap",
        endpointInterface= "org.atricore.idbus.common.oauth._2_0.wsdl.OAuthPortType")
public class OAuth2ServiceImpl implements OAuthPortType {

    public AccessTokenResponseType accessTokenRequest(AccessTokenRequestType body) {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }


    public AuthorizationResponseType authorizationRequest(AuthorizationRequestType body) {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    public SendPasswordlessLinkResponseType sendPasswordlessLinkRequest(SendPasswordlessLinkRequestType body) {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }
}
