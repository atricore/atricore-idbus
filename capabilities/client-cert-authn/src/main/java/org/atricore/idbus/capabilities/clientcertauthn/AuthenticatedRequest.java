package org.atricore.idbus.capabilities.clientcertauthn;

public class AuthenticatedRequest {
    private byte[] certValue;

    public AuthenticatedRequest(byte[] certValue) {
        this.certValue = certValue;
    }

    public byte[] getCertValue() {
        return certValue;
    }

    public void setCertValue(byte[] certValue) {
        this.certValue = certValue;
    }
}
