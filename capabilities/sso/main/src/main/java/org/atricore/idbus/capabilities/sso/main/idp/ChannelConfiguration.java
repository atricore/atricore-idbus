package org.atricore.idbus.capabilities.sso.main.idp;

import java.io.Serializable;

/**
 *
 */
public class ChannelConfiguration implements Serializable {

    private String name;

    private boolean encryptAssertion;

    private String encryptAssertionAlgorithm;

    private String signatureAlgorithm;

    public ChannelConfiguration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEncryptAssertion() {
        return encryptAssertion;
    }

    public void setEncryptAssertion(boolean encryptAssertion) {
        this.encryptAssertion = encryptAssertion;
    }

    public String getEncryptAssertionAlgorithm() {
        return encryptAssertionAlgorithm;
    }

    public void setEncryptAssertionAlgorithm(String encryptAssertionAlgorithm) {
        this.encryptAssertionAlgorithm = encryptAssertionAlgorithm;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }
}
