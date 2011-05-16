package org.atricore.idbus.kernel.main.store.identity;

import org.atricore.idbus.kernel.main.authn.SSOPasswordPolicy;
import org.atricore.idbus.kernel.main.authn.SSOPolicy;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BindContext {

    void addPasswordPolicyMessages(SSOPasswordPolicy msg);

    List<SSOPolicy> getSSOPolicies();
}
