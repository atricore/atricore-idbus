package com.atricore.idbus.console.activation.main.client.impl;

import com.atricore.idbus.console.activation._1_0.protocol.*;
import com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType;
import com.atricore.idbus.console.activation.main.client.ActivationClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivationClientImpl implements ActivationClient {

    private static final Log logger = LogFactory.getLog(ActivationClientImpl.class);
    
    public static final QName SERVICE_NAME = new QName("urn:com:atricore:idbus:console:activation:1.0:wsdl", "ActivationService");

    public static final QName PORT_NAME = new QName("urn:com:atricore:idbus:console:activation:1.0:wsdl", "soap");

    public static final String SERVICE_PATH = "/atricore-console/services/activation";
    
    private String endpoint;

    public ActivationClientImpl(String location) {
        this(location, SERVICE_PATH);
    }
    
    public ActivationClientImpl(String location, String servicePath) {
        location = location.endsWith("/") ? location.substring(0, location.length() -1) : location;
        servicePath = !servicePath.startsWith("/") ? "/" + servicePath : servicePath;
        
        this.endpoint = location + servicePath;
        
        if (logger.isDebugEnabled())
            logger.debug("Using service endpoint : " + endpoint);
        
    }
    

    public ConfigureAgentResponseType configureAgent(ConfigureAgentRequestType request) {
        return getSoapPort(endpoint).configureAgent(request);
    }

    public ActivateAgentResponseType activateAgent(ActivateAgentRequestType request) {
        return getSoapPort(endpoint).activateAgent(request);
    }

    public ActivateSamplesResponseType activateSamples(ActivateSamplesRequestType request) {
        return getSoapPort(endpoint).activateSamples(request);
    }

    public PlatformSupportedResponseType platformSupported(PlatformSupportedRequestType request) {
        return getSoapPort(endpoint).platformSupported(request);
    }
    
    protected ActivationPortType getSoapPort(String endpoint) {
        Service service = Service.create(SERVICE_NAME);
        service.addPort(PORT_NAME, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING, endpoint);
        
        return service.getPort(PORT_NAME, ActivationPortType.class);
        
    }
}
