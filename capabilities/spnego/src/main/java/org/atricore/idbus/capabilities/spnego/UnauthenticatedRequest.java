package org.atricore.idbus.capabilities.spnego;

public class UnauthenticatedRequest implements SpnegoMessage {

    private boolean spnegoAvailable;

    public UnauthenticatedRequest() {
    }

    public UnauthenticatedRequest(boolean spnegoAvailable) {
        this.spnegoAvailable = spnegoAvailable;
    }

    public boolean isSpnegoAvailable() {
        return spnegoAvailable;
    }

    public void setSpnegoAvailable(boolean spnegoAvailable) {
        this.spnegoAvailable = spnegoAvailable;
    }
}
