package org.atricore.idbus.capabilities.openidconnect.main.binding;

import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;

/**
 * Created by sgonzalez on 3/11/14.
 */
public enum OpenIDConnectBinding {

    // Binding URIs for native openid endpoints
    OPENID_HTTP_POST("urn:OPENID-CONNECT:1.0:bindings:HTTP-POST", true),
    OPENIDCONNECT_AUTHZ("urn:OPENID-CONNECT:1.0:bindings:AUTHZ", true),

    // Binding URIs for non-native openid endpoints
    SSO_REDIRECT(SSOBinding.SSO_REDIRECT.getValue(), SSOBinding.SSO_REDIRECT.isFrontChannel()),
    SSO_ARTIFACT(SSOBinding.SSO_ARTIFACT.getValue(), SSOBinding.SSO_ARTIFACT.isFrontChannel())
    ;

    private String binding;
    boolean frontChannel;

    OpenIDConnectBinding (String binding, boolean frontChannel) {
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

    public static OpenIDConnectBinding  asEnum(String binding) {
        for (OpenIDConnectBinding  b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid OpenIDConnectBinding '" + binding + "'");
    }
}
