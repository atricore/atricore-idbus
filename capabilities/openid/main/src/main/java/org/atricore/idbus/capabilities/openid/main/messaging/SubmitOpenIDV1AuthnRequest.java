package org.atricore.idbus.capabilities.openid.main.messaging;

public class SubmitOpenIDV1AuthnRequest extends OpenIDMessage {

    private String destinationUrl;

    public SubmitOpenIDV1AuthnRequest(String version, String destinationUrl) {
        super(version);

        this.destinationUrl = destinationUrl;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

}
