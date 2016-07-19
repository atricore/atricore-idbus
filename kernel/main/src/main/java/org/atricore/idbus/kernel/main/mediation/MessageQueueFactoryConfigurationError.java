package org.atricore.idbus.kernel.main.mediation;

/**
 * Created by sgonzalez.
 */
public class MessageQueueFactoryConfigurationError extends Exception {

    public MessageQueueFactoryConfigurationError() {
    }

    public MessageQueueFactoryConfigurationError(String message) {
        super(message);
    }

    public MessageQueueFactoryConfigurationError(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageQueueFactoryConfigurationError(Throwable cause) {
        super(cause);
    }

    public MessageQueueFactoryConfigurationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
