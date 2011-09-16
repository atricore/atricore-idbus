package org.atricore.idbus.idojos.impersonateusrauthscheme;

import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CurrentUserValidationCredential extends BaseCredential {

    public CurrentUserValidationCredential() {
        super();
    }

    public CurrentUserValidationCredential(Object credential) {
        super(credential);
    }

}

