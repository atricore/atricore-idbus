package org.atricore.idbus.capabilities.openid.main.messaging;

public class SubmitOpenIDAuthnRequest implements OpenIDMessage {


    private String version;
    private String destinationUrl;

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
