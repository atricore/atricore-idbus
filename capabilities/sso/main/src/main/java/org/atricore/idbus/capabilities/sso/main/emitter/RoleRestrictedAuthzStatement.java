package org.atricore.idbus.capabilities.sso.main.emitter;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;

public class RoleRestrictedAuthzStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:authz-group-restricted";

    public static final String NAME = "authzGroupRestricted";

    private String requiredRole;

    public RoleRestrictedAuthzStatement(String requiredRole) {
        super(NAMESPACE, NAME);
        this.requiredRole = requiredRole;
    }

    public String getRequiredRole() {
        return requiredRole;
    }


}
