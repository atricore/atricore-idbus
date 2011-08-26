package org.atricore.idbus.capabilities.openid.main.messaging;

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
