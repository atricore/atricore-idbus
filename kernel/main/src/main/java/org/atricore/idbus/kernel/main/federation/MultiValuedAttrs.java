package org.atricore.idbus.kernel.main.federation;

public enum MultiValuedAttrs {

    /**
     * All attributes can be multi-valued, including internal generated attributes like idpName, idpAlias
     */
    INTERNAL,
    /**
     * Groups attributes are treated as multivalued
     */
    GROUPS,
    /**
     * User defined attributes are treated as multivualued
     */
    USER_DEFINED
}
