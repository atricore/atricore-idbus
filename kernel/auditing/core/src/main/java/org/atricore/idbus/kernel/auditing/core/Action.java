package org.atricore.idbus.kernel.auditing.core;

public enum Action {

    /**
     * IDP SSO, SSO Action performed by an IDP
     */
    SSO("SSO"),

    /**
     * IDP SLO, SLO Action performed by an IDP
     */
    SLO("SLO"),

    /**
     * IDP SLO, IDP SLO Action due to Session Timout
     */
    SLO_TOUT("SLO-TOUT"),

    /**
     * IDP Proxy SSO, SSO Action performed by an IDP Proxy
     */
    PXY_SSO("PXY-SSO"),

    /**
     * IDP Proxy SLO, SLO Action performed by an IDP Proxy
     */
    PXY_SLO("PXY-SLO"),

    /**
     * IDP Proxy SLO, IDP Proxy SLO Action due to Session Timout
     */
    PXY_SLO_TOUT("PXY-SLO-TOUT"),

    /**
     * IDP SLO REQUEST, SLO REQUEST processed by an IDP
     */
    SLOR("SLO-REQ"),

    /**
     * SP SSO, SSO Action processed by an SP
     */
    SP_SSO("SP-SSO"),

    /**
     * SP SLO, SLO Action processed by an SP
     */
    SP_SLO("SP-SLO"),


    /**
     * SP SSO RESPONSE, SSO Response Processed by an SP
     */
    SP_SSOR("SP-SSOR"),

    /**
     * SP SLO RESPONSE, SLO Response Processed by an SP
     */
    SP_SLOR("SP-SLOR");

    private String value;

    private Action(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
}
