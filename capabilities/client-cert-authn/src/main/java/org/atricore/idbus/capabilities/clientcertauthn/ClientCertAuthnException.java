package org.atricore.idbus.capabilities.clientcertauthn;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientCertAuthnException extends Exception {

    public ClientCertAuthnException() {
        super();
    }

    public ClientCertAuthnException(String message) {
        super(message);
    }

    public ClientCertAuthnException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientCertAuthnException(Throwable cause) {
        super(cause);
    }
}
