package org.atricore.idbus.capabilities.spnego;

public class InitiateSpnegoNegotiation implements SpnegoMessage {
    private String targetSpnegoEndpoint;

    public InitiateSpnegoNegotiation(String targetSpnegoEndpoint) {
        this.targetSpnegoEndpoint = targetSpnegoEndpoint;
    }

    public String getTargetSpnegoEndpoint() {
        return targetSpnegoEndpoint;
    }

    public void setTargetSpnegoEndpoint(String targetSpnegoEndpoint) {
        this.targetSpnegoEndpoint = targetSpnegoEndpoint;
    }




}
