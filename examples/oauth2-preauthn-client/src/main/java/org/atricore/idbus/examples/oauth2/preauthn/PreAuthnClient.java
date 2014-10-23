package org.atricore.idbus.examples.oauth2.preauthn;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenResponseType;
import org.atricore.idbus.common.oauth._2_0.wsdl.OAuthPortType;

import java.io.IOException;

/**
 * OAuth2 Sample  Client, represents a client application that needs to access a resource server.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PreAuthnClient {

    private String clientId;

    private String clientSecret;

    private String endpoint;

    private OAuthPortType wsClient;

    private String defaultUser;

    private String defaultPassword;

    public PreAuthnClient(String clientId, String clientSecret, String endpoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.endpoint = endpoint;
        this.wsClient = doMakeWsClient();
    }

    public String requestTokenForUsernamePassword(String username, String password) throws Exception {

        // Build OAuth2 AccessToken request
        AccessTokenRequestType req = new AccessTokenRequestType();
        req.setClientId(clientId);
        req.setClientSecret(clientSecret);
        req.setUsername(username);
        req.setPassword(password);

        // Request a Token
        AccessTokenResponseType res = wsClient.accessTokenRequest(req);

        if (res.getError() != null) {
            throw new Exception("Cannot get access token: " + res.getError().value() + " ["+res.getErrorDescription()+"]");
        }

        return res.getAccessToken();
    }

    protected OAuthPortType doMakeWsClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(OAuthPortType.class);
        factory.setAddress(endpoint);

        OAuthPortType client = (OAuthPortType) factory.create();

        return client;

    }

}
