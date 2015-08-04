package org.atricore.idbus.capabilities.openidconnect.main.common;

import org.atricore.idbus.capabilities.sts.main.WSTConstants;

/**
 *
 */
public enum OpenIDConnectTokenType implements java.io.Serializable {

    AUTHZ_CODE ("code", WSTConstants.WST_OIDC_AUTHZ_CODE_TYPE),
    ACCESS_TOKEN("token", WSTConstants.WST_OAUTH2_TOKEN_TYPE),
    ID_TOKEN("id_token", WSTConstants.WST_OIDC_ID_TOKEN_TYPE);

    private String name;

    private String fqtn;

    OpenIDConnectTokenType(String name, String fqtn) {
        this.name = name;
        this.fqtn = fqtn;
    }

    public String getName() {
        return name;
    }

    public String getFQTN() {
        return fqtn;
    }

    public static OpenIDConnectTokenType asEnum(String name) {
        for (OpenIDConnectTokenType tt : OpenIDConnectTokenType.values()) {
            if (tt.getName().equals(name))
                return tt;
        }
        throw new IllegalArgumentException("Invalid token name " + name);
    }

    public static OpenIDConnectTokenType asEnumFromFQTN(String fqtn) {
        for (OpenIDConnectTokenType tt : OpenIDConnectTokenType.values()) {
            if (tt.getFQTN().equals(fqtn))
                return tt;
        }
        throw new IllegalArgumentException("Invalid token FQTN " + fqtn);

    }
}
