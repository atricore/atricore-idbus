package org.atricore.idbus.capabilities.atricoreid.as.main.binding;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum AtricoreIDBinding {

    /** URI for IDBUS SOAP binding, this is NOT SAML Normtive */
    OAUTH2_SOAP("urn:org:atricore:idbus:OAUTH:2.0:bindings:SOAP", false),

    OAUTH2_RESTFUL("urn:org:atricore:idbus:OAUTH:2.0:bindings:HTTP-Restful", true);


    private String binding;
    boolean frontChannel;

    AtricoreIDBinding(String binding, boolean frontChannel) {
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

    public static AtricoreIDBinding asEnum(String binding) {
        for (AtricoreIDBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid AtricoreIDBinding '" + binding + "'");
    }

}
