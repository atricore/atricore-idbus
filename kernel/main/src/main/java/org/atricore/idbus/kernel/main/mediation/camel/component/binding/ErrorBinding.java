package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

public enum ErrorBinding {

    ARTIFACT("urn:org:atricore:idbus:error:bindings:HTTP-Artifact"),
    JSON("urn:org:atricore:idbus:error:bindings:HTTP-POST");

    private String binding;

    ErrorBinding(String binding) {
        this.binding = binding;
    }

    public String getValue() {
        return binding;
    }

    @Override
    public String toString() {
        return binding;
    }

    public static ErrorBinding asEnum(String binding) {
        for (ErrorBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid ErrorBinding '" + binding + "'");
    }
}
