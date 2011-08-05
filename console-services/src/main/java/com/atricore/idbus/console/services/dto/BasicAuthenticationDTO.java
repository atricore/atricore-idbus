package com.atricore.idbus.console.services.dto;

public class BasicAuthenticationDTO extends AuthenticationMechanismDTO {

    private static final long serialVersionUID = -1338282044516771281L;

    private String hashAlgorithm;

    private String hashEncoding;

    private boolean ignoreUsernameCase = false;

    private boolean ignorePasswordCase = false;

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
}