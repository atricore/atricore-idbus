package org.atricore.idbus.capabilities.samlr2.main.binding.services;

import oasis.names.tc.saml._1_0.wsdl.SAMLRequestPortType;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@javax.jws.WebService(
                      serviceName = "SAMLService",
                      portName = "soap",
                      targetNamespace = "urn:oasis:names:tc:SAML:1.0:wsdl",
                      endpointInterface = "oasis.names.tc.saml._1_0.wsdl.SAMLRequestPortType")
public class SamlR11ServiceImpl implements SAMLRequestPortType {

    private static final Logger LOG = Logger.getLogger(SamlR11ServiceImpl.class.getName());

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._1_0.wsdl.SAMLRequestPortType#samlRequest(oasis.names.tc.saml._1_0.protocol.RequestType  body )*
     */
    public oasis.names.tc.saml._1_0.protocol.ResponseType samlRequest(oasis.names.tc.saml._1_0.protocol.RequestType body) {
        LOG.info("Executing operation samlRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

}
