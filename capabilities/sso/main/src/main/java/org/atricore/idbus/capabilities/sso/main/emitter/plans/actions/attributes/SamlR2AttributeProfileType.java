package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

/**
 *
 */
public enum SamlR2AttributeProfileType {

    BASIC("Basic", "urn:oasis:names:tc:SAML:2.0:profiles:attribute:basic"),
    JOSSO("JOSSO", "urn:org:atricore:SAML:2.0:profiles:attribute:josso"),
    ONE_TO_ONE("JOSSO", "urn:org:atricore:SAML:2.0:profiles:attribute:one-to-one"),
    CUSTOM("CUSTOM", "urn:org:atricore:SAML:2.0:profiles:attribute:custom");

    private String displayName;

    private String name;

    SamlR2AttributeProfileType(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public static SamlR2AttributeProfileType asEnum(String name) {
        for (SamlR2AttributeProfileType profile : values()) {
            if (profile.getName().equals(name)) {
                return profile;
            }
        }
        return null;
    }
}
