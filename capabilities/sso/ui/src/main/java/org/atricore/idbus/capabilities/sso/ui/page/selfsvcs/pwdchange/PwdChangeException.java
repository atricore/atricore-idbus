package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/30/13
 */
public class PwdChangeException extends Exception {

    private String messageKey;

    public PwdChangeException(String messageKey) {
        this(messageKey, "Password change error");
    }

    public PwdChangeException(String messageKey, String error) {
        super(error);
        this.messageKey = messageKey;
    }

    public PwdChangeException(String messageKey, String error, Throwable cause) {
        super(error, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

}

