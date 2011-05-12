package org.atricore.idbus.kernel.main.authn.scheme;

import org.atricore.idbus.kernel.main.authn.SSOPasswordPolicy;
import org.atricore.idbus.kernel.main.store.identity.BindContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BindContextImpl implements BindContext, Serializable {

    private List<SSOPasswordPolicy> ppolicies = new ArrayList<SSOPasswordPolicy>();

    public void addPasswordPolicyMessages(SSOPasswordPolicy msg) {
        ppolicies.add(msg);
    }

    public List<SSOPasswordPolicy> getPasswordPolicyMessages() {
        return ppolicies;
    }
}
