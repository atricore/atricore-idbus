package org.atricore.idbus.examples.oauth2.preauthn;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Sample {

    protected Properties config;

    protected PreAuthnClient client;

    protected ResourceServer rServer;

    protected String username;
    protected String password;


    public static void main(String args[]) throws Exception {
        OAuth2Sample sample = new OAuth2Sample();

        sample.init();

        sample.start();
    }

    public void init() throws IOException {


        // Load confiuration information
        config = new Properties();
        config.load(getClass().getResourceAsStream("/oauth2.properties"));

        // Create client instance
        client = new PreAuthnClient(
            config.getProperty("oauth2.clientId"),
            config.getProperty("oauth2.clientSecret"),
            config.getProperty("oauth2.token.endpoint"));

        // Load test credentials
        username = config.getProperty("oauth2.token.username");
        password = config.getProperty("oauth2.token.password");

        // Create resource server (not required for authentication)
        rServer = new ResourceServer (config);

    }

    public void start() throws Exception {

        try {

            // Authenticate user
            String token = authenticate(username, password);

            // Authorize resource access
            String resourceId = "foo";
            boolean authorized = authorize(resourceId, token);

            // Build resource URL
            String oauth2Resource = config.getProperty("oauth2.resource");
            token = URLEncoder.encode(token, "UTF-8");
            System.out.println("Authorized [" + authorized + " ] : " + authorized);
            System.out.println("\nResource URL: \n" + oauth2Resource + "&access_token=" + token);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }


    }

    public String authenticate(String usr, String pwd) throws Exception {
        String token = client.requestTokenForUsernamePassword(usr, pwd);
        return token;
    }

    public boolean authorize(String resourceId, String token) throws Exception {
        return rServer.authorize(resourceId, token);
    }

}
