package org.atricore.idbus.kernel.auditing.core;

public enum Action {

    SSO("SSO"),
    SLO("SLO"),
    SLOR("SLOR"),
    SP_SSO("SP-SSO"),
    SP_SLO("SP-SLO"),
    SP_SSOR("SP-SSOR"),
    SP_SLOR("SP-SLOR"),
    SLO_TOUT("SLO-TOUT");

    private String value;

    private Action(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
}
