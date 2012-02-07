package org.atricore.idbus.capabilities.atricoreid.as.main;

import java.util.StringTokenizer;

/**
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDClient {

    private String id;

    private String secret;

    // TODO : other AtricoreID properties  (URLs, etc) ....


    public AtricoreIDClient() {
    }

    public AtricoreIDClient(String id, String secret) {
        this.id = id;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "clientId:" + id + ", clientSecret:" + (secret != null ? "*" : "null");
    }
}
