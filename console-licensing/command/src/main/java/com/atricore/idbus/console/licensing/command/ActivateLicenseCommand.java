package com.atricore.idbus.console.licensing.command;

import com.atricore.idbus.console.licensing.main.LicenseManager;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "licensing", name = "activate", description = "Activate new product license")
public class ActivateLicenseCommand extends LicenseCommandSupport {

    String licenseFile;

    @Override
    protected Object doExecute(LicenseManager svc) throws Exception {

        // TODO : Impelement me

        return null;
    }
}
