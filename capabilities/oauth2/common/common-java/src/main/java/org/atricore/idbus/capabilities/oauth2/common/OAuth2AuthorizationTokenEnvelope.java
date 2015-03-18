package org.atricore.idbus.capabilities.oauth2.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AuthorizationTokenEnvelope {

    private String encryptionAlg;

    private String signatureAlg;

    private String signatureValue;

    private String token;

    private boolean deflated;

    public OAuth2AuthorizationTokenEnvelope(String encryptionAlg, String signatureAlg, String signatureValue, String token, boolean deflated) {
        this.encryptionAlg = encryptionAlg;
        this.signatureAlg = signatureAlg;
        this.signatureValue = signatureValue;
        this.token = token;
        this.deflated = deflated;
    }

    public OAuth2AuthorizationTokenEnvelope() {
    }

    public String getEncryptionAlg() {
        return encryptionAlg;
    }

    public void setEncryptionAlg(String encryptionAlg) {
        this.encryptionAlg = encryptionAlg;
    }

    public String getSignatureAlg() {
        return signatureAlg;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public String getToken() {
        return token;
    }

    public boolean isDeflated() {
        return deflated;
    }

    public void setSignatureAlg(String signatureAlg) {
        this.signatureAlg = signatureAlg;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDeflated(boolean deflated) {
        this.deflated = deflated;
    }
}
