package org.atricore.idbus.idojos.impersonateusrauthscheme;

import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ImpersonatedUsernameCredential extends BaseCredential {

    public ImpersonatedUsernameCredential() {
        super();
    }

    public ImpersonatedUsernameCredential(Object credential) {
        super(credential);
    }
}
