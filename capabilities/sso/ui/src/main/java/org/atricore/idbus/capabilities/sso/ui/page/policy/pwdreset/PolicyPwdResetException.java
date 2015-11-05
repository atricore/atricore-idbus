package org.atricore.idbus.capabilities.sso.ui.page.policy.pwdreset;

public class PolicyPwdResetException extends Exception {

    private String messageKey;

    public PolicyPwdResetException(String messageKey) {
        this(messageKey, "Password reset error");
    }

    public PolicyPwdResetException(String messageKey, String error) {
        super(error);
        this.messageKey = messageKey;
    }

    public PolicyPwdResetException(String messageKey, String error, Throwable cause) {
        super(error, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
