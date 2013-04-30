package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/29/13
 */
public class RegistrationState implements java.io.Serializable {

    private int retries;

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
