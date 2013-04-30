package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/29/13
 */
public class RegistrationException extends Exception {

    private String messageKey;

    public RegistrationException(String messageKey) {
        this(messageKey, "Registration error");
    }

    public RegistrationException(String messageKey, String error) {
        super(error);
        this.messageKey = messageKey;
    }

    public RegistrationException(String messageKey, String error, Throwable cause) {
        super(error, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

}
