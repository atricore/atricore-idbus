package com.atricore.idbus.console.lifecycle.main.spi.response;

import java.util.Date;

public class GetCertificateInfoResponse extends AbstractManagementResponse {

    // signing certificate
    private String signingCertIssuerDN;
    private String signingCertSubjectDN;
    private Date signingCertNotBefore;
    private Date signingCertNotAfter;

    // encryption certificate
    private String encryptionCertIssuerDN;
    private String encryptionCertSubjectDN;
    private Date encryptionCertNotBefore;
    private Date encryptionCertNotAfter;

    public String getEncryptionCertIssuerDN() {
        return encryptionCertIssuerDN;
    }

    public void setEncryptionCertIssuerDN(String encryptionCertIssuerDN) {
        this.encryptionCertIssuerDN = encryptionCertIssuerDN;
    }

    public Date getEncryptionCertNotAfter() {
        return encryptionCertNotAfter;
    }

    public void setEncryptionCertNotAfter(Date encryptionCertNotAfter) {
        this.encryptionCertNotAfter = encryptionCertNotAfter;
    }

    public Date getEncryptionCertNotBefore() {
        return encryptionCertNotBefore;
    }

    public void setEncryptionCertNotBefore(Date encryptionCertNotBefore) {
        this.encryptionCertNotBefore = encryptionCertNotBefore;
    }

    public String getEncryptionCertSubjectDN() {
        return encryptionCertSubjectDN;
    }

    public void setEncryptionCertSubjectDN(String encryptionCertSubjectDN) {
        this.encryptionCertSubjectDN = encryptionCertSubjectDN;
    }

    public String getSigningCertIssuerDN() {
        return signingCertIssuerDN;
    }

    public void setSigningCertIssuerDN(String signingCertIssuerDN) {
        this.signingCertIssuerDN = signingCertIssuerDN;
    }

    public Date getSigningCertNotAfter() {
        return signingCertNotAfter;
    }

    public void setSigningCertNotAfter(Date signingCertNotAfter) {
        this.signingCertNotAfter = signingCertNotAfter;
    }

    public Date getSigningCertNotBefore() {
        return signingCertNotBefore;
    }

    public void setSigningCertNotBefore(Date signingCertNotBefore) {
        this.signingCertNotBefore = signingCertNotBefore;
    }

    public String getSigningCertSubjectDN() {
        return signingCertSubjectDN;
    }

    public void setSigningCertSubjectDN(String signingCertSubjectDN) {
        this.signingCertSubjectDN = signingCertSubjectDN;
    }
}
