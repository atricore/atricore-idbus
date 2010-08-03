package org.atricore.idbus.capabilities.samlr2.main.binding.services;

import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.common.sso._1_0.wsdl.SSORequestPortType;

import javax.jws.WebParam;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@javax.jws.WebService(
                      serviceName = "SSOService",
                      portName = "soap",
                      targetNamespace = "urn:org:atricore:idbus:common:sso:1.0:wsdl",
                      endpointInterface = "org.atricore.idbus.common.sso._1_0.wsdl.SSORequestPortType")
public class SSOServiceImpl implements SSORequestPortType {

    private static final Logger LOG = Logger.getLogger(SSOServiceImpl.class.getName());

    public IDPSessionHeartBeatResponseType idpSessionHeartBeatRequest(@WebParam(partName = "body", name = "IDPSessionHeartBeatRequest", targetNamespace = "urn:org:atricore:idbus:common:sso:1.0:protocol") IDPSessionHeartBeatRequestType body) {
        LOG.info("Executing operation idpSessionHeartBeatRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SPSessionHeartBeatResponseType spSessionHeartBeatRequest(@WebParam(partName = "body", name = "SPSessionHeartBeatRequest", targetNamespace = "urn:org:atricore:idbus:common:sso:1.0:protocol") SPSessionHeartBeatRequestType body) {
        LOG.info("Executing operation spSessionHeartBeatRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SSOResponseType idpInitiatedLogoutRequest(@WebParam(partName = "body", name = "IDPInitiatedLogoutRequest", targetNamespace = "urn:org:atricore:idbus:common:sso:1.0:protocol") IDPInitiatedLogoutRequestType body) {
        LOG.info("Executing operation idpInitiatedLogoutRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SSOResponseType spInitiatedLogoutRequest(SPInitiatedLogoutRequestType body) {
        LOG.info("Executing operation spInitiatedLogoutRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SPAuthnResponseType assertIdentityWithSimpleAuthenticationRequest(AssertIdentityWithSimpleAuthenticationRequestType body) {
        LOG.info("Executing operation assertIdentityWithSimpleAuthenticationRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public IDPAuthnResponseType idpInitiatedAuthnRequest(@WebParam(partName = "body", name = "IDPInitiatedAuthnRequest", targetNamespace = "urn:org:atricore:idbus:common:sso:1.0:protocol") IDPInitiatedAuthnRequestType body) {
        LOG.info("Executing operation idpInitiatedAuthnRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SPAuthnResponseType spInitiatedAuthnRequest(SPInitiatedAuthnRequestType body) {
        LOG.info("Executing operation spInitiatedAuthnRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }
}
