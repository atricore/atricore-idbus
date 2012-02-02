package org.atricore.idbus.capabilities.oauth2.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ResourceServer {

    private String resourceLocation;

    private String sharedSecret;

    private String accessTokenParam = "access_token";

    private String name;

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public String getAccessTokenParam() {
        return accessTokenParam;
    }

    public void setAccessTokenParam(String accessTokenParam) {
        this.accessTokenParam = accessTokenParam;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
