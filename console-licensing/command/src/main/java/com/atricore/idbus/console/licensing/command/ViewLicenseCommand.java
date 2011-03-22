package com.atricore.idbus.console.licensing.command;

import com.atricore.idbus.console.licensing.main.LicenseManager;
import org.apache.felix.gogo.commands.Command;

/**
* @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "licensing", name = "view", description = "View Active license information")
public class ViewLicenseCommand extends LicenseCommandSupport {

    // If set to true, display general EULA (and if a feature is selected, the feature license text)
    boolean verbose;

    // Optional, if null display all features.
    String feature;

    @Override
    protected Object doExecute(LicenseManager svc) throws Exception {
        // TODO : Implement me

        // Display :
        return null;
    }
}
