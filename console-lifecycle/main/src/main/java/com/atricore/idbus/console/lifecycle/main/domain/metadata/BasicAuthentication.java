package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BasicAuthentication extends AuthenticationMechanism {

    private static final long serialVersionUID = -1338282044516771281L;

    private String hashAlgorithm;

    private String hashEncoding;

    private boolean ignoreUsernameCase = false;

    private boolean ignorePasswordCase = false;

    // TODO : For now this is bound to basic authn.  When multiple authn mechanisms are supported, it should be a mechanism on its own.
    private ImpersonateUserPolicy impersonateUserPolicy;

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

    public boolean isIgnorePasswordCase() {
        return ignorePasswordCase;
    }

    public void setIgnorePasswordCase(boolean ignorePasswordCase) {
        this.ignorePasswordCase = ignorePasswordCase;
    }

    public ImpersonateUserPolicy getImpersonateUserPolicy() {
        return impersonateUserPolicy;
    }

    public void setImpersonateUserPolicy(ImpersonateUserPolicy impersonateUserPolicy) {
        this.impersonateUserPolicy = impersonateUserPolicy;
    }
}
