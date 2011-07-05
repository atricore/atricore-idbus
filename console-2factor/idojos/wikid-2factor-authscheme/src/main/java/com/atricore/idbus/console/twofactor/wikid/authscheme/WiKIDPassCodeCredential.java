package com.atricore.idbus.console.twofactor.wikid.authscheme;

import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WiKIDPassCodeCredential extends BaseCredential {

    public WiKIDPassCodeCredential(String passcode) {
        super(passcode);
    }

    public WiKIDPassCodeCredential() {
        super();
    }
}
