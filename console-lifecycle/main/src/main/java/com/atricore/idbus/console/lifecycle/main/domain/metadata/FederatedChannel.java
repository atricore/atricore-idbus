package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FederatedChannel extends Channel {

    private static final long serialVersionUID = 4578962395672061313L;

    private FederatedConnection connectionA;

    private FederatedConnection connectionB;

    public FederatedConnection getConnectionA() {
        return connectionA;
    }

    public void setConnectionA(FederatedConnection connectionA) {
        this.connectionA = connectionA;
    }

    public FederatedConnection getConnectionB() {
        return connectionB;
    }

    public void setConnectionB(FederatedConnection connectionB) {
        this.connectionB = connectionB;
    }
}
