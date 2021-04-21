package org.atricore.idbus.kernel.main.authn.scheme;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSOPasswordPolicyEnforcement;
import org.atricore.idbus.kernel.main.store.identity.BindContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BindContextImpl implements BindContext, Serializable {

    private List<PolicyEnforcementStatement> ppolicies = new ArrayList<PolicyEnforcementStatement>();

    public void addPasswordPolicyMessages(SSOPasswordPolicyEnforcement msg) {
        ppolicies.add(msg);
    }

    public void addPolicyEnforcementStatement(PolicyEnforcementStatement msg) {
        ppolicies.add(msg);
    }


    public List<PolicyEnforcementStatement> getSSOPolicies() {
        return ppolicies;
    }
}
