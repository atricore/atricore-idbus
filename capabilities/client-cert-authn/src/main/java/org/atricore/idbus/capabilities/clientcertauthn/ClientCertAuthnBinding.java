package org.atricore.idbus.capabilities.clientcertauthn;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ClientCertAuthnBinding {

    /** URI for IDBUS HTTP Artifact binding, this is NOT SAML Normative*/
    SSO_ARTIFACT("urn:org:atricore:idbus:sso:bindings:HTTP-Artifact"),

    /** URI for SPNEGO over HTTP Binding */
    CLIENT_CERT_AUTHN_HTTPS_CLAIMS("urn:org:atricore:idbus:client-cert-authn:bindings:HTTPS-INITIATION");

    private String binding;

    ClientCertAuthnBinding(String binding) {
        this.binding = binding;
    }

    public String getValue() {
        return binding;
    }

    @Override
    public String toString() {
        return binding;
    }

    public static ClientCertAuthnBinding asEnum(String binding) {
        for (ClientCertAuthnBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid Client Certificate Authentication Binding '" + binding + "'");
    }
}
