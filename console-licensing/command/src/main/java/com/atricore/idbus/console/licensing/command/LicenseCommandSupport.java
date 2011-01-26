package com.atricore.idbus.console.licensing.command;

import com.atricore.idbus.console.licensing.main.LicenseManager;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class LicenseCommandSupport extends OsgiCommandSupport {

    @Override
    protected Object doExecute() throws Exception {
        // Get service reference and all execute again
        LicenseManager svc = null;

        return doExecute(svc);
    }

    protected abstract Object doExecute(LicenseManager svc) throws Exception ;

}
