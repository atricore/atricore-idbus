package org.atricore.idbus.capabilities.oath;

import org.atricore.idbus.kernel.main.authn.Credential;

import java.io.Serializable;

public class OTPSecret implements Credential {

    private String value;

    public OTPSecret(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
