package org.atricore.idbus.capabilities.atricoreid.as.main.binding.services;

import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AccessTokenRequestType;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AccessTokenResponseType;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AuthorizationRequestType;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AuthorizationResponseType;
import org.atricore.idbus.capabilities.atricoreid._1_0.wsdl.AtricoreIDPortType;

import javax.jws.WebService;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@WebService(
        targetNamespace = "urn:org:atricore:idbus:common:oauth:2.0:wsdl",
        serviceName = "AtricoreIDService",
        portName = "soap",
        endpointInterface= "org.atricore.idbus.capabilities.atricoreid._1_0.wsdl.AtricoreIDPortType")
public class AtricoreIDServiceImpl implements AtricoreIDPortType {

    public AccessTokenResponseType accessTokenRequest(AccessTokenRequestType body) {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }


    public AuthorizationResponseType authorizationRequest(AuthorizationRequestType body) {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

}
