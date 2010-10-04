package org.atricore.idbus.kernel.common.support.jdbc;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDBCManagerException extends Exception {

    public JDBCManagerException() {
        super();
    }

    public JDBCManagerException(String message) {
        super(message);
    }

    public JDBCManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public JDBCManagerException(Throwable cause) {
        super(cause);
    }
}
