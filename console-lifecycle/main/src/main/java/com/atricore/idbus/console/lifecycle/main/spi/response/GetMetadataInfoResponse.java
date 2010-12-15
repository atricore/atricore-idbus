package com.atricore.idbus.console.lifecycle.main.spi.response;

import java.util.Date;

public class GetMetadataInfoResponse extends AbstractManagementResponse {

    private String entityId;

    // profiles
    private boolean ssoEnabled;
    private boolean sloEnabled;

    // bindings
    private boolean postEnabled;
    private boolean redirectEnabled;
    private boolean artifactEnabled;
    private boolean soapEnabled;

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
    
    public GetMetadataInfoResponse() {
        super();
        ssoEnabled = false;
        sloEnabled = false;
        postEnabled = false;
        redirectEnabled = false;
        artifactEnabled = false;
        soapEnabled = false;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean isSloEnabled() {
        return sloEnabled;
    }

    public void setSloEnabled(boolean sloEnabled) {
        this.sloEnabled = sloEnabled;
    }

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public void setSsoEnabled(boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }

    public boolean isArtifactEnabled() {
        return artifactEnabled;
    }

    public void setArtifactEnabled(boolean artifactEnabled) {
        this.artifactEnabled = artifactEnabled;
    }

    public boolean isPostEnabled() {
        return postEnabled;
    }

    public void setPostEnabled(boolean postEnabled) {
        this.postEnabled = postEnabled;
    }

    public boolean isRedirectEnabled() {
        return redirectEnabled;
    }

    public void setRedirectEnabled(boolean redirectEnabled) {
        this.redirectEnabled = redirectEnabled;
    }

    public boolean isSoapEnabled() {
        return soapEnabled;
    }

    public void setSoapEnabled(boolean soapEnabled) {
        this.soapEnabled = soapEnabled;
    }

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
