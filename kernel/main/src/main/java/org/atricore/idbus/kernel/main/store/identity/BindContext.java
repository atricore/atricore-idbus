package org.atricore.idbus.kernel.main.store.identity;

import org.atricore.idbus.kernel.main.authn.SSOPasswordPolicyEnforcement;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BindContext {

    void addPasswordPolicyMessages(SSOPasswordPolicyEnforcement msg);

    void addPolicyEnforcementStatement(PolicyEnforcementStatement msg);

    List<PolicyEnforcementStatement> getSSOPolicies();
}
