package org.atricore.idbus.capabilities.spnego;

import java.io.Serializable;

public class AuthenticatedRequest implements SpnegoMessage {
    private byte[] tokenValue;

    AuthenticatedRequest(byte[] tokenValue) {
        this.tokenValue = tokenValue;
    }

    public byte[] getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(byte[] tokenValue) {
        this.tokenValue = tokenValue;
    }
}
