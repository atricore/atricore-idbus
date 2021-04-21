package org.atricore.idbus.capabilities.preauthn.binding;

import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;

public enum PreAuthnBinding {

    SSO_ARTIFACT(SSOBinding.SSO_ARTIFACT.getValue(), SSOBinding.SSO_ARTIFACT.isFrontChannel()),

    SSO_REDIRECT(SSOBinding.SSO_REDIRECT.getValue(), SSOBinding.SSO_REDIRECT.isFrontChannel()),

    SSO_PREAUTHN(SSOBinding.SSO_PREAUTHN.getValue(), SSOBinding.SSO_PREAUTHN.isFrontChannel());


    private String binding;
    boolean frontChannel;

    PreAuthnBinding(String binding, boolean frontChannel) {
        this.binding = binding;
        this.frontChannel = frontChannel;
    }

    public String getValue() {
        return binding;
    }

    @Override
    public String toString() {
        return binding;
    }

    public boolean isFrontChannel() {
        return frontChannel;
    }

    public static PreAuthnBinding asEnum(String binding) {
        for (PreAuthnBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid PreAuthnBinding '" + binding + "'");
    }

}
