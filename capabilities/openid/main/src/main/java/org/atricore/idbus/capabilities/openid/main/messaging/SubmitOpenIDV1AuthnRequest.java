package org.atricore.idbus.capabilities.openid.main.messaging;

/**
 * Message type for representing Authentication requests for an OpenID version 1 identity provider
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
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
