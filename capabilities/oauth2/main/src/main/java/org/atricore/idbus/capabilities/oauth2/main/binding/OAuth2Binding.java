package org.atricore.idbus.capabilities.oauth2.main.binding;

import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum OAuth2Binding {

    SSO_ARTIFACT(SSOBinding.SSO_ARTIFACT.getValue(), SSOBinding.SSO_ARTIFACT.isFrontChannel()),

    SSO_REDIRECT(SSOBinding.SSO_REDIRECT.getValue(), SSOBinding.SSO_REDIRECT.isFrontChannel()),

    SSO_PREAUTHN(SSOBinding.SSO_PREAUTHN.getValue(), SSOBinding.SSO_PREAUTHN.isFrontChannel()),

    SSO_SOAP(SSOBinding.SSO_SOAP.getValue(), SSOBinding.SSO_SOAP.isFrontChannel()),

    SSO_LOCAL(SSOBinding.SSO_LOCAL.getValue(), SSOBinding.SSO_LOCAL.isFrontChannel()),

    OAUTH2_SOAP("urn:org:atricore:idbus:OAUTH:2.0:bindings:SOAP", false),

    OAUTH2_RESTFUL("urn:org:atricore:idbus:OAUTH:2.0:bindings:HTTP-Restful", true);


    private String binding;
    boolean frontChannel;

    OAuth2Binding(String binding, boolean frontChannel) {
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

    public static OAuth2Binding asEnum(String binding) {
        for (OAuth2Binding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid OAuth2Binding '" + binding + "'");
    }

}
