package org.atricore.idbus.capabilities.openidconnect.main.common.binding;

import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;

/**
 * Created by sgonzalez on 3/11/14.
 */
public enum OpenIDConnectBinding {

    // Binding URIs for native openid endpoints
    OPENID_HTTP_POST("urn:OPENID-CONNECT:1.0:bindings:HTTP-POST", true),

    OPENIDCONNECT_AUTHZ("urn:OPENID-CONNECT:1.0:bindings:AUTHZ", true),


    OPENID_PROVIDER_AUTHZ_HTTP("urn:net:openidconnect:1.0:op:bindings:authz:http", true),
    OPENID_PROVIDER_AUTHZ_RESTFUL("urn:net:openidconnect:1.0:op:bindings:authz:restful", false),
    OPENID_PROVIDER_LOGOUT_HTTP("urn:net:openidconnect:1.0:op:bindings:logout:http", true),
    OPENID_PROVIDER_TOKEN_HTTP("urn:net:openidconnect:1.0:op:bindings:token:http", false),
    OPENID_PROVIDER_TOKEN_RESTFUL("urn:net:openidconnect:1.0:op:bindings:token:restful", false),
    OPENID_PROVIDER_INFO_RESTFUL("urn:net:openidconnect:1.0:op:bindings:provider-info:restful", false),
    OPENID_PROVIDER_USERINFO_RESTFUL("urn:net:openidconnect:1.0:op:bindings:user-info:restful", false),
    OPENID_PROVIDER_CHKSESSION_IFRAME_RESTFUL("urn:net:openidconnect:1.0:op:bindings:chk-session-iframe:restful", false),
    OPENID_PROVIDER_STATE_RESTFUL("urn:net:openidconnect:1.0:op:bindings:op-state:restful", false),
    OPENID_PROVIDER_JWK_RESTFUL("urn:net:openidconnect:1.0:op:bindings:jwk:restful", false),

    // Binding URIs for non-native openid endpoints used to communicate with other capabilities
    SSO_REDIRECT(SSOBinding.SSO_REDIRECT.getValue(), SSOBinding.SSO_REDIRECT.isFrontChannel()),
    SSO_ARTIFACT(SSOBinding.SSO_ARTIFACT.getValue(), SSOBinding.SSO_ARTIFACT.isFrontChannel()),
    SSO_LOCAL(SSOBinding.SSO_LOCAL.getValue(), SSOBinding.SSO_LOCAL.isFrontChannel()),
    SSO_PAYLOAD("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Payload", true),
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
