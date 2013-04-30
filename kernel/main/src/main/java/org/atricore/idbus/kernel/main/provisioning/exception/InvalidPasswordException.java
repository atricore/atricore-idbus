package org.atricore.idbus.kernel.main.provisioning.exception;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/30/13
 */
public class InvalidPasswordException extends ProvisioningException {

    public InvalidPasswordException() {
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }
}
