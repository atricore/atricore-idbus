package org.atricore.idbus.capabilities.oauth2.client;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenResponseType;
import org.atricore.idbus.common.oauth._2_0.protocol.SendPasswordlessLinkRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.SendPasswordlessLinkResponseType;
import org.atricore.idbus.common.oauth._2_0.wsdl.OAuthPortType;

/**
 * OAuth2 Sample  AccessTokenRequestor, represents a requestor application that needs to access a resource server.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccessTokenRequestor extends AbstractWSClient {

    public AccessTokenRequestor(String clientId, String clientSecret, String endpoint, String wsdlLocation) {
        super(clientId, clientSecret, endpoint, wsdlLocation);
    }

    public String requestTokenForUsernamePassword(String username, String password) throws Exception {

        // Build OAuth2 AccessToken request
        AccessTokenRequestType req = new AccessTokenRequestType();
        req.setClientId(getClientId());
        req.setClientSecret(getClientSecret());
        req.setUsername(username);
        req.setPassword(password);

        // Request a Token
        AccessTokenResponseType res = getWsClient().accessTokenRequest(req);

        if (res.getError() != null) {
            String errMsg = "Cannot get access token: " + res.getError().value() + " ["+res.getErrorDescription()+"]";
            if (res.getSsoPolicyEnforcements() != null && res.getSsoPolicyEnforcements().size() > 0) {
                throw new OAuth2ClientException(errMsg, res.getSsoPolicyEnforcements());
            }
            throw new Exception(errMsg);
        }

        return res.getAccessToken();
    }

}
