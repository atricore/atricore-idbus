package org.atricore.idbus.capabilities.sso.main.idp;

/**
 *
 */
public class ChannelConfiguration {

    private String name;

    private boolean encryptAssertion;

    private String encryptAssertionAlgorithm;

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

}
