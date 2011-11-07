package org.atricore.idbus.capabilities.oauth2.main.binding;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum OAuth2Binding {

    /** URI for IDBUS SOAP binding, this is NOT SAML Normtive */
    OAUTH2_SOAP("urn:org:atricore:idbus:OAUTH:2.0:bindings:SOAP", false),

    OAUTH2_RESTFUL("urn:org:atricore:idbus:OAUTH:2.0:bindings:RESTFUL", true);


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
