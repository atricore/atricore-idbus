package com.atricore.idbus.console.services.dto;

import java.util.Set;

/**
 * Author: Dejan Maric
 */
public class FederatedProviderDTO extends ProviderDTO {

    private static final long serialVersionUID = 1096573395672061313L;

    private Set<FederatedConnectionDTO> federatedConnectionsA;

    private Set<FederatedConnectionDTO> federatedConnectionsB;

    public Set<FederatedConnectionDTO> getFederatedConnectionsA() {
        return federatedConnectionsA;
    }

    public void setFederatedConnectionsA(Set<FederatedConnectionDTO> federatedConnectionsA) {
        this.federatedConnectionsA = federatedConnectionsA;
    }

    public Set<FederatedConnectionDTO> getFederatedConnectionsB() {
        return federatedConnectionsB;
    }

    public void setFederatedConnectionsB(Set<FederatedConnectionDTO> federatedConnectionsB) {
        this.federatedConnectionsB = federatedConnectionsB;
    }
}
