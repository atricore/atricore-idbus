package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class FederatedProvider extends LocalProvider {

    private static final long serialVersionUID = 1096573395672061313L;
    
    private Set<FederatedConnection> federatedConnectionsA;

    private Set<FederatedConnection> federatedConnectionsB;

    public Set<FederatedConnection> getFederatedConnectionsA() {
        return federatedConnectionsA;
    }

    public void setFederatedConnectionsA(Set<FederatedConnection> federatedConnectionsA) {
        this.federatedConnectionsA = federatedConnectionsA;
    }

    public Set<FederatedConnection> getFederatedConnectionsB() {
        return federatedConnectionsB;
    }

    public void setFederatedConnectionsB(Set<FederatedConnection> federatedConnectionsB) {
        this.federatedConnectionsB = federatedConnectionsB;
    }
}
