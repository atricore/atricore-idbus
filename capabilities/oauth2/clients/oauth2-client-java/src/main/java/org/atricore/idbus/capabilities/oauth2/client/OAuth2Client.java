package org.atricore.idbus.capabilities.oauth2.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Client {

    protected Properties config;

    protected AccesssTokenRequestor requestor;

    public static void main(String args[]) throws Exception {
        OAuth2Client client = new OAuth2Client();
        client.init();
        client.start();
    }

    public void init() throws IOException {
        config = new Properties();
        config.load(getClass().getResourceAsStream("/oauth2.properties"));

        requestor = new AccesssTokenRequestor(
                config.getProperty("oauth2.clientId"),
                config.getProperty("oauth2.clientSecret"),
                config.getProperty("oauth2.authorizationServerEndpoint"));
    }

    public void start() throws Exception {
        String accessToken = requestor.requestTokenForUsernamePassword(
                config.getProperty("oauth2.username"),
                config.getProperty("oauth2.password"));

        String spAlias = config.getProperty("oauth2.serviceProviderAlias");
        String resourceServerEndpoint = config.getProperty("oauth2.resourceServerEndpoint");

        String preauthUrl =
                String.format("%s?atricore_sp_alias=%s&atricore_security_token=%s",
                    resourceServerEndpoint,
                    spAlias,
                    URLEncoder.encode(accessToken, "UTF-8")
                );

        System.out.println(preauthUrl);
    }

}
