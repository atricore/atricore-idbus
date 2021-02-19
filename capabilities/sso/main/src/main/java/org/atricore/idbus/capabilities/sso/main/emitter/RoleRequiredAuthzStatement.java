package org.atricore.idbus.capabilities.sso.main.emitter;


import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;

public class RoleRequiredAuthzStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:policy:authz-group-required";

    public static final String NAME = "authzGroupRequired";

    private String requiredRole;

    public RoleRequiredAuthzStatement(String requiredRole) {
        super(NAMESPACE, NAME);
        this.requiredRole = requiredRole;
    }

    public String getRequiredRole() {
        return requiredRole;
    }


}
