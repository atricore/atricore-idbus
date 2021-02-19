package org.atricore.idbus.capabilities.sso.main.idp;

import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;

import java.util.List;

/**
 *
 */
public class SPChannelConfiguration extends ChannelConfiguration {

    private boolean encryptAssertion;

    private String encryptAssertionAlgorithm;

    private String signatureHash;

    private List<SPChannelRBACRule> rbac;

    public SPChannelConfiguration(String name) {
        super(name);
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

    public String getSignatureHash() {
        return signatureHash;
    }

    public void setSignatureHash(String signatureHash) {
        this.signatureHash = signatureHash;
    }

    public List<SPChannelRBACRule> getRbac() {
        return rbac;
    }

    public void setRbac(List<SPChannelRBACRule> rbac) {
        this.rbac = rbac;
    }
}
