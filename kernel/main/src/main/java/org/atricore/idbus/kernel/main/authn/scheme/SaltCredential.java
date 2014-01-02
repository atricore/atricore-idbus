package org.atricore.idbus.kernel.main.authn.scheme;

import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SaltCredential extends BaseCredential {

    public SaltCredential() {
        super();
    }

    public SaltCredential(Object credential) {
        super(credential);
    }

}
