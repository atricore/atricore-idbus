package org.atricore.idbus.kernel.main.mediation.camel.component.http;

public enum XFrameOptions {

    DISABLED("DISABLED"),
    SAME_ORIGIN("SAMEORIGIN"),
    ALLOW_FROM("ALLOW-FROM"),
    DENY("DENY");

    private String value;

    XFrameOptions(String value) {
        this.value = value;
    }

    public static XFrameOptions fromValue(String value) {
        for (XFrameOptions f : XFrameOptions.values()) {
            if (f.getValue().equals(value))
                return f;
        }

        throw new IllegalArgumentException("Invalid value " + value);
    }

    public String getValue() {
        return value;
    }
}
