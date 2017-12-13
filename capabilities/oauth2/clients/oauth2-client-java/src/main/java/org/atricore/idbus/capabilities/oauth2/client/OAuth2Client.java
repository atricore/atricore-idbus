package org.atricore.idbus.capabilities.oauth2.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * This client can be used to request OAuth2 access tokens. It can also create the proper URLs to either access a
 * resource protected by an OAuth2 resource server or to trigger the pre-authentication process.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Client implements ConfigurationConstants {

    protected Properties config;

    protected String configPath;

    protected AccessTokenRequestor requestor;

    private boolean init;

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
                    config.getProperty(CLIENT_ID),
                    config.getProperty(CLIENT_SECRET),
                    config.getProperty(AUTHN_ENDPOINT),
                    config.getProperty(WSDL_LOCATION));

            requestor.setLogMessages(Boolean.parseBoolean(config.getProperty(LOG_MESSAGES, "false")));

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
     * Requests an authorization token for the given username and password.
     */
    public String requestToken(String usr, String pwd) throws OAuth2ClientException {

        try {
            String accessToken = getAccessTokenRequestor().requestTokenForUsernamePassword(usr, pwd);
            return accessToken;
        } catch (OAuth2ClientException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuth2ClientException(e);
        }
    }

    /**
     * Builds a pre-authentication Url for the given username and password, and requesting
     * the default SP (as configured in the oauth2.spAlias property).
     *
     * This method calls the requestToken method.
     *
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    @Deprecated
    public String buildgetIdPInitPreAuthnUrlForDefaultSp(String usr, String pwd) throws OAuth2ClientException {
        String spAlias = config.getProperty(SP_ALIAS);
        return buildIdPInitPreAuthnUrl(spAlias, usr, pwd);
    }


    /**
     * Builds a pre-authentication Url for the given username and password, and requesting
     * the default SP (as configured in the oauth2.spAlias property).
     *
     * This method calls the requestToken method.
     *
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    @Deprecated
    public String buildIdPInitPreAuthnUrlForDefaultSp(String usr, String pwd) throws OAuth2ClientException {
        String spAlias = config.getProperty(SP_ALIAS);
        return buildIdPInitPreAuthnUrl(spAlias, usr, pwd);
    }


    /**
     * Builds a pre-authentication Url for the given username and password.
     *
     * This method calls the requestToken method.
     *
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    public String buildIdPInitPreAuthnUrl(String usr, String pwd) throws OAuth2ClientException {
        return buildIdPInitPreAuthnUrl(null, usr, pwd);
    }


    /**
     * Builds a pre-authentication Url for the given username and password.
     *
     * This method calls the requestToken method.
     *
     * @oaran relayState as received with the pre-authn token request.
     * @param spAlias SAML SP ALias, null if no specific SP is required or known.
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    public String buildIdPInitPreAuthnUrl(String spAlias, String usr, String pwd) throws OAuth2ClientException {

        try {
            String accessToken = requestToken(usr, pwd);
            String idpPreAuthn = config.getProperty(IDP_INIT_PREAUTHN_ENDPOINT);

            // TODO : Relay on SsoPreauthTokenSvcBinding in the future
            String preauthUrl =
                    String.format("%s?atricore_security_token=%s&scope=preauth-token",
                            idpPreAuthn,
                            URLEncoder.encode(accessToken, "UTF-8")
                    );

            if (spAlias != null)
                preauthUrl += "&atricore_sp_alias=" + spAlias;

            return preauthUrl;

        } catch (UnsupportedEncodingException e) {
            throw new OAuth2ClientException(e);
        }
    }


    /**
     * Builds a pre-authentication Url for the given username and password.
     *
     * This method calls the requestToken method.
     *
     * @oaran relayState as received with the pre-authn token request.
     * @param usr the username used to issue the access token
     * @param pwd the password used to issue the access token
     */
    public String buildIdPPreAuthnResponseUrl(String relayState, String usr, String pwd) throws OAuth2ClientException {

        try {
            String accessToken = requestToken(usr, pwd);
            String idpPreAuthn = config.getProperty(IDP_PREAUTHN_RESPONSE_ENDPOINT);

            // TODO : Relay on SsoPreauthTokenSvcBinding in the future
            String preauthUrl =
                    String.format("%s?atricore_security_token=%s&scope=preauth-token",
                            idpPreAuthn,
                            URLEncoder.encode(accessToken, "UTF-8")
                    );

            if (relayState != null)
                preauthUrl += "&relay_state=" + relayState;

            return preauthUrl;

        } catch (UnsupportedEncodingException e) {
            throw new OAuth2ClientException(e);
        }
    }

    /**
     * Builds a pre-authentication Url for the given access token.
     *
     * This method calls the requestToken method.
     *
     * @oaran relayState as received with the pre-authn token request.
     * @param accessToken oauth2 access token
     */
    public String buildIdPPreAuthnResponseUrl(String relayState, String accessToken) throws OAuth2ClientException {

        try {
            String idpPreAuthn = config.getProperty(IDP_PREAUTHN_RESPONSE_ENDPOINT);

            // TODO : Relay on SsoPreauthTokenSvcBinding in the future
            String preauthUrl =
                    String.format("%s?atricore_security_token=%s&scope=preauth-token",
                            idpPreAuthn,
                            URLEncoder.encode(accessToken, "UTF-8")
                    );

            if (relayState != null)
                preauthUrl += "&relay_state=" + relayState;

            return preauthUrl;
        } catch (UnsupportedEncodingException e) {
            throw new OAuth2ClientException(e);
        }

    }

    /**
     * Builds a pre-authentication Url for the given access token.
     *
     * This method calls the requestToken method.
     *
     * @oaran relayState as received with the pre-authn token request.
     * @param accessToken oauth2 access token
     */
    public String buildIdPPreAuthnResponseUrlForSP(String relayState, String accessToken, String spAlias) throws OAuth2ClientException {

        try {
            String idpPreAuthn = config.getProperty(IDP_PREAUTHN_RESPONSE_ENDPOINT);

            // TODO : Relay on SsoPreauthTokenSvcBinding in the future
            String preauthUrl =
                    String.format("%s?atricore_security_token=%s&scope=preauth-token",
                            idpPreAuthn,
                            URLEncoder.encode(accessToken, "UTF-8")
                    );

            if (relayState != null)
                preauthUrl += "&relay_state=" + relayState;

            if (spAlias != null)
                preauthUrl += "&atricore_sp_alias=" + spAlias;

            return preauthUrl;
        } catch (UnsupportedEncodingException e) {
            throw new OAuth2ClientException(e);
        }

    }

    /**
     * Builds a pre-authentication Url for the given username and password.
     *
     * This method calls the requestToken method.
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


    protected Properties loadConfig() throws IOException, OAuth2ClientException {

        if (configPath == null)
            configPath = "/oauth2.properties";

        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream(configPath);
        if (is == null)
            throw new OAuth2ClientException("Configuration not found for " + configPath);

        props.load(is);
        return props;
    }

}
