package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.io.Serializable;

public class PwdResetState implements Serializable {

    private User user;

    private String transactionId;

    public PwdResetState(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
