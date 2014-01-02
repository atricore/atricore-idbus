package com.atricore.idbus.console.services.dto;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.ImpersonateUserPolicy;

public class BasicAuthenticationDTO extends AuthenticationMechanismDTO {

    private static final long serialVersionUID = -1338282044516771281L;

    private String hashAlgorithm;

    private String hashEncoding;

    private boolean ignoreUsernameCase = false;

    private boolean ignorePasswordCase = false;

    private int saltLength = 0;

    // TODO : For now this is bound to basic authn.  When multiple authn mechanisms are supported, it should be a mechanism on its own.
    private ImpersonateUserPolicyDTO impersonateUserPolicy;

    private boolean enabled;

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

    public ImpersonateUserPolicyDTO getImpersonateUserPolicy() {
        return impersonateUserPolicy;
    }

    public void setImpersonateUserPolicy(ImpersonateUserPolicyDTO impersonateUserPolicy) {
        this.impersonateUserPolicy = impersonateUserPolicy;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSaltLength() {
        return saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }
}