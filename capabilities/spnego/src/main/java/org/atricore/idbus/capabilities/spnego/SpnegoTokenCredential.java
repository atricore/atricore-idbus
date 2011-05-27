package org.atricore.idbus.capabilities.spnego;

import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class SpnegoTokenCredential extends BaseCredential {

    public SpnegoTokenCredential(String spnegoToken) {
        super(spnegoToken);
    }

    public SpnegoTokenCredential() {
        super();
    }
}
