package org.atricore.idbus.capabilities.csca;

public enum CscaBinding {

    /** URI for IDBUS HTTP Artifact binding, this is NOT SAML Normative*/
    SSO_ARTIFACT("urn:org:atricore:idbus:sso:bindings:HTTP-Artifact");

    private String binding;

    CscaBinding(String binding) {
        this.binding = binding;
    }

    public String getValue() {
        return binding;
    }

    @Override
    public String toString() {
        return binding;
    }

    public static CscaBinding asEnum(String binding) {
        for (CscaBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid Client-side Certitificate Authentication (CSCA) Binding '" + binding + "'");
    }
}
