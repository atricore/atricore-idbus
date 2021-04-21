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
    SP_SLOR("SP-SLOR"),

    /**
     * Password Reset
     */
    PWD_RESET("PWD-RESET"),

    /**
     * Prepare Password Reset
     */
    PREPARE_PWD_RESET("PREPARE-PWD-RESET"),

    /**
     * Confirm Password Reset
     */
    CONFIRM_PWD_RESET("CONFIRM-PWD-RESET"),

    /**
     * Add User
     */
    ADD_USER("ADD-USER"),

    /**
     * Add User (BATCH)
     */
    ADD_USER_BATCH("ADD-USER-BATCH"),

    /**
     * Prepare Add User
     */
    PREPARE_ADD_USER("PREPARE-ADD-USER"),

    /**
     * Confirm Add User
     */
    CONFIRM_ADD_USER("CONFIRM-ADD-USER"),

    /**
     * Update User
     */
    UPDATE_USER("UPDATE-USER"),

    /**
     * Update User (BATCH)
     */
    UPDATE_USERS("UPDATE-USER-BATCH"),

    /**
     * Remove User
     */
    REMOVE_USER("REMOVE-USER"),

    /**
     * Remove User (BATCH)
     */
    REMOVE_USERS("REMOVE-USER-BATCH"),

    /**
     * Add User Attribute
     */
    ADD_USER_ATTRIBUTE("ADD-USER-ATTRIBUTE"),

    /**
     * Update User Attribute
     */
    UPDATE_USER_ATTRIBUTE("UPDATE-USER-ATTRIBUTE"),

    /**
     * Remove User Attribute
     */
    REMOVE_USER_ATTRIBUTE("REMOVE-USER-ATTRIBUTE"),

    /**
     * Add Group
     */
    ADD_GROUP("ADD-GROUP"),

    /**
     * Update Group
     */
    UPDATE_GROUP("UPDATE-GROUP"),

    /**
     * Remove Group
     */
    REMOVE_GROUP("REMOVE-GROUP"),

    /**
     * Add Group Attribute
     */
    ADD_GROUP_ATTRIBUTE("ADD-GROUP-ATTRIBUTE"),

    /**
     * Update Group Attribute
     */
    UPDATE_GROUP_ATTRIBUTE("UPDATE-GROUP-ATTRIBUTE"),

    /**
     * Remove Group Attribute
     */
    REMOVE_GROUP_ATTRIBUTE("REMOVE-GROUP-ATTRIBUTE"),

    /**
     * SPML Password Reset
     */
    SPML_PWD_RESET("SPML-PWD-RESET"),

    /**
     * SPML Prepare Password Reset
     */
    SPML_PREPARE_PWD_RESET("SPML-PREPARE-PWD-RESET"),

    /**
     * SPML Confirm Password Reset
     */
    SPML_CONFIRM_PWD_RESET("SPML-CONFIRM-PWD-RESET"),

    /**
     * SPML Add User
     */
    SPML_ADD_USER("SPML-ADD-USER"),

    /**
     * SPML Update User
     */
    SPML_UPDATE_USER("SPML-UPDATE-USER"),

    /**
     * SPML Remove User
     */
    SPML_REMOVE_USER("SPML-REMOVE-USER"),

    /**
     * SPML Add User Attribute
     */
    SPML_ADD_USER_ATTRIBUTE("SPML-ADD-USER-ATTRIBUTE"),

    /**
     * SPML Update User Attribute
     */
    SPML_UPDATE_USER_ATTRIBUTE("SPML-UPDATE-USER-ATTRIBUTE"),

    /**
     * SPML Remove User Attribute
     */
    SPML_REMOVE_USER_ATTRIBUTE("SPML-REMOVE-USER-ATTRIBUTE"),

    /**
     * SPML Add Group
     */
    SPML_ADD_GROUP("SPML-ADD-GROUP"),

    /**
     * SPML Update Group
     */
    SPML_UPDATE_GROUP("SPML-UPDATE-GROUP"),

    /**
     * SPML Remove Group
     */
    SPML_REMOVE_GROUP("SPML-REMOVE-GROUP"),

    /**
     * SPML Add Group Attribute
     */
    SPML_ADD_GROUP_ATTRIBUTE("SPML-ADD-GROUP-ATTRIBUTE"),

    /**
     * SPML Update Group Attribute
     */
    SPML_UPDATE_GROUP_ATTRIBUTE("SPML-UPDATE-GROUP-ATTRIBUTE"),

    /**
     * SPML Remove Group Attribute
     */
    SPML_REMOVE_GROUP_ATTRIBUTE("SPML-REMOVE-GROUP-ATTRIBUTE");

    private String value;

    private Action(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
}
