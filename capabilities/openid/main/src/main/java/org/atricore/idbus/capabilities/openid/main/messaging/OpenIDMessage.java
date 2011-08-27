package org.atricore.idbus.capabilities.openid.main.messaging;

/**
 * Message type for representing OpenID Messages
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OpenIDMessage {

    private String version;

    protected OpenIDMessage(String version) {
        this.version = version;
    }

    private OpenIDMessage() {
    }

    public String getVersion() {
        return version;
    }

}
