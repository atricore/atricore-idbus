package org.atricore.idbus.capabilities.spmlr2.main.binding;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum SpmlR2Binding {

    /** Non-normative, only useful between local providers, for perfomrance issues */
    SPMLR2_SOAP("urn:oasis:names:tc:SPML:2:0:bindings:SOAP", false),

    /** Non-normative, only useful between local providers, for perfomrance issues */
    SPMLR2_LOCAL("urn:oasis:names:tc:SPML:2:0:bindings:LOCAL", false)
    
    ;

    private String binding;
    boolean frontChannel;

    SpmlR2Binding(String binding, boolean frontChannel) {
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

    public static SpmlR2Binding asEnum(String binding) {
        for (SpmlR2Binding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid SpmlR2Binding '" + binding + "'");
    }


}
