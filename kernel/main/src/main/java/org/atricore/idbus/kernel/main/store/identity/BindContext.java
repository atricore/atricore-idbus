package org.atricore.idbus.kernel.main.store.identity;

import org.atricore.idbus.kernel.main.authn.SSOPasswordPolicy;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BindContext {

    void addPasswordPolicyMessages(SSOPasswordPolicy msg);

    List<SSOPasswordPolicy> getPasswordPolicyMessages();
}
