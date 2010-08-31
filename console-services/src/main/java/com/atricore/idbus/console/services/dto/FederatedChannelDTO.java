package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class FederatedChannelDTO extends ChannelDTO {

    private static final long serialVersionUID = 4578962395672061313L;

    private FederatedConnectionDTO connectionA;

    private FederatedConnectionDTO connectionB;

    public FederatedConnectionDTO getConnectionA() {
        return connectionA;
    }

    public void setConnectionA(FederatedConnectionDTO connectionA) {
        this.connectionA = connectionA;
    }

    public FederatedConnectionDTO getConnectionB() {
        return connectionB;
    }

    public void setConnectionB(FederatedConnectionDTO connectionB) {
        this.connectionB = connectionB;
    }
}
