package org.atricore.idbus.capabilities.oauth2.client;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.atricore.idbus.common.oauth._2_0.wsdl.OAuthPortType;

public class AbstractWSClient {

    private String clientId;

    private String clientSecret;

    private String endpoint;

    private String wsdlLocation;

    private OAuthPortType wsClient;

    private boolean logMessages;

    public AbstractWSClient(String clientId, String clientSecret, String endpoint, String wsdlLocation) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.endpoint = endpoint;
        this.wsdlLocation = wsdlLocation;
        this.wsClient = doMakeWsClient();
    }

    public boolean isLogMessages() {
        return logMessages;
    }

    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

    protected String getClientId() {
        return clientId;
    }

    protected String getClientSecret() {
        return clientSecret;
    }

    protected String getEndpoint() {
        return endpoint;
    }

    protected String getWsdlLocation() {
        return wsdlLocation;
    }

    protected OAuthPortType getWsClient() {
        return wsClient;
    }

    protected OAuthPortType doMakeWsClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        if (logMessages) {
            factory.getInInterceptors().add(new LoggingInInterceptor());
            factory.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        factory.setServiceClass(OAuthPortType.class);
        factory.setAddress(endpoint);
        if (wsdlLocation != null && !wsdlLocation.equals(""))
            factory.setWsdlLocation(wsdlLocation);

        OAuthPortType client = (OAuthPortType) factory.create();

        return client;

    }
}
