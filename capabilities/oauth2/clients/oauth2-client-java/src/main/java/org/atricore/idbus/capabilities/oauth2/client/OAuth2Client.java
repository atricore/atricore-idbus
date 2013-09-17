package org.atricore.idbus.capabilities.oauth2.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * This client can be used to request OAuth2 access tokens. It can also create the proper URLs to either access a
 * resource protected by an OAuth2 resource server or to trigger the pre-authentication process.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Client {

    protected Properties config;

    protected String configPath;

    protected AccessTokenRequestor requestor;

    private boolean init;

    public static void main(String args[]) throws Exception {
        OAuth2Client client = new OAuth2Client();
        client.init();

        System.out.println("IDP pre-authn  : " + client.getIdPPreAuthnUrl());
        System.out.println("OAuth2 resource: " + client.getResourceUrl());
    }

    public OAuth2Client(String configPath) {
        this.configPath = configPath;
    }

    public OAuth2Client(Properties config) {
        this.config = config;
    }

    public OAuth2Client() {

    }

    /**
     * Initializes the client by loading the configuration if necessary.
     *
     * @throws OAuth2ClientException if an error occurs while loading the configuration.
     */
    public void init() throws OAuth2ClientException {

        try {

            if (config == null)
                config = loadConfig();

            requestor = new AccessTokenRequestor(
                    config.getProperty("oauth2.clientId"),
                    config.getProperty("oauth2.clientSecret"),
                    config.getProperty("oauth2.authorizationServerEndpoint"));

            init = true;
        } catch (IOException e) {
            throw new OAuth2ClientException(e);
        }
    }

    public AccessTokenRequestor getAccessTokenRequestor() throws OAuth2ClientException {

        if (!init) {
            throw new OAuth2ClientException("OAuth2 client not initialized");
        }

        return requestor;
    }

    /**
     * Requests an authorization token for the configured username and password.
     */
    public String requestToken() throws OAuth2ClientException {
        return requestToken(
                config.getProperty("oauth2.username"),
                config.getProperty("oauth2.password"));
    }

    /**
     * Requests an authorization token for the given username and password.
     */
    public String requestToken(String usr, String pwd) throws OAuth2ClientException {

        try {
            String accessToken = getAccessTokenRequestor().requestTokenForUsernamePassword(usr, pwd);
            return accessToken;
        } catch (Exception e) {
            throw new OAuth2ClientException(e);
        }
    }

    /**
     * Builds a pre-authentication Url for the configured username and password.
     */
    public String getIdPPreAuthnUrl() throws OAuth2ClientException {
        return getIdPPreAuthnUrl(
                config.getProperty("oauth2.username"),
                config.getProperty("oauth2.password"));
    }

    /**
     * Builds a pre-authentication Url for the given username and password.
     *
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    public String getIdPPreAuthnUrl(String usr, String pwd) throws OAuth2ClientException {
        String spAlias = config.getProperty("oauth2.serviceProviderAlias");
        return getIdPPreAuthnUrl(spAlias, usr, pwd)
    }

    public String getIdPPreAuthnUrl(String spAlias, String usr, String pwd) throws OAuth2ClientException {

        try {
            String accessToken = requestToken(usr, pwd);
            String resourceServerEndpoint = config.getProperty("oauth2.identityProviderPreAuthnEndpoint");
            String preauthUrl =
                    String.format("%s?atricore_sp_alias=%s&atricore_security_token=%s",
                            resourceServerEndpoint,
                            spAlias,
                            URLEncoder.encode(accessToken, "UTF-8")
                    );

            return preauthUrl;
        } catch (UnsupportedEncodingException e) {
            throw new OAuth2ClientException(e);
        }
    }


    /**
     * Builds a resource Url for the configured username and password.
     */
    public String getResourceUrl() throws OAuth2ClientException {
        return getResourceUrl(
                config.getProperty("oauth2.resourceServerEndpoint"),
                config.getProperty("oauth2.username"),
                config.getProperty("oauth2.password"));
    }

    /**
     * Builds a pre-authentication Url for the given username and password.
     *
     * @param resource the resource server URL we want to access
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    public String getResourceUrl(String resource, String usr, String pwd) throws OAuth2ClientException {

        try {
            String accessToken = requestToken(usr, pwd);
            String preauthUrl =
                    String.format("%s?access_token=%s",
                            resource,
                            URLEncoder.encode(accessToken, "UTF-8")
                    );

            return preauthUrl;
        } catch (UnsupportedEncodingException e) {
            throw new OAuth2ClientException(e);
        }
    }


    protected Properties loadConfig() throws IOException {

        if (configPath == null)
            configPath = "/oauth2.properties";
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/oauth2.properties"));
        return props;
    }

}
