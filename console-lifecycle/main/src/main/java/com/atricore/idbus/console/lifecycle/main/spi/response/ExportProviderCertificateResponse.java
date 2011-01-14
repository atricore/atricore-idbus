package com.atricore.idbus.console.lifecycle.main.spi.response;

public class ExportProviderCertificateResponse extends AbstractManagementResponse {

    private String providerId;

    private byte[] certificate;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }
}
