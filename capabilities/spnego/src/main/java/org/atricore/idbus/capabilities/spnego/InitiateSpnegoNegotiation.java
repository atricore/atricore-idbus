package org.atricore.idbus.capabilities.spnego;

public class InitiateSpnegoNegotiation implements SpnegoMessage {

    public InitiateSpnegoNegotiation(String spnegoInitiationEndpoint) {
        this.spnegoInitiationEndpoint = spnegoInitiationEndpoint;
    }

    public String getSpnegoInitiationEndpoint() {
        return spnegoInitiationEndpoint;
    }

    public void setSpnegoInitiationEndpoint(String spnegoInitiationEndpoint) {
        this.spnegoInitiationEndpoint = spnegoInitiationEndpoint;
    }

    private String spnegoInitiationEndpoint;

}
