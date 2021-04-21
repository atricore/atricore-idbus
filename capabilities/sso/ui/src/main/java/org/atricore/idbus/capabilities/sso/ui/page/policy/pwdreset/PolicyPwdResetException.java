package org.atricore.idbus.capabilities.sso.ui.page.policy.pwdreset;

public class PolicyPwdResetException extends Exception {

    private String[] messageKeys;

    public PolicyPwdResetException(String[] messageKeys) {
        this(messageKeys, "Password reset error");
    }

    public PolicyPwdResetException(String[] messageKeys, String error) {
        super(error);
        this.messageKeys = messageKeys;
    }

    public PolicyPwdResetException(String[] messageKeys, String error, Throwable cause) {
        super(error, cause);
        this.messageKeys = messageKeys;
    }

    public String[] getMessageKeys() {
        return messageKeys;
    }
}
