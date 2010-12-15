package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BasicAuthentication extends AuthenticationMechanism {

    private static final long serialVersionUID = -1338282044516771281L;

    private String hashAlgorithm;

    private String hashEncoding;

    private boolean ignoreUsernameCase = false;

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getHashEncoding() {
        return hashEncoding;
    }

    public void setHashEncoding(String hashEncoding) {
        this.hashEncoding = hashEncoding;
    }

    public boolean isIgnoreUsernameCase() {
        return ignoreUsernameCase;
    }

    public void setIgnoreUsernameCase(boolean ignoreUsernameCase) {
        this.ignoreUsernameCase = ignoreUsernameCase;
    }
}
