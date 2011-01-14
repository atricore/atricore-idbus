package com.atricore.idbus.console.lifecycle.main.spi.response;

public class ExportProviderCertificateResponse extends AbstractManagementResponse {

    private byte[] certificate;

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }
}
