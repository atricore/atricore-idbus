package com.atricore.idbus.console.licensing.command;

import com.atricore.idbus.console.licensing.command.printers.LicenseCmdPrinter;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
* @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "licensing", name = "view", description = "View Active license information")
public class ViewLicenseCommand extends LicenseCommandSupport {

    // If set to true, display general EULA (and if a feature is selected, the feature license text)
    @Option(name = "-v", aliases = "--verbose", description = "Displays EULA or feature license text if feature is selected", required = false, multiValued = false)
    boolean verbose = false;

    // Optional, if null display all features.
    @Option(name = "-f", aliases = "--feature", description = "Select the feature to show", required = false, multiValued = false)
    String feature;

    @Override
    protected Object doExecute(LicenseManager svc) throws Exception {
        LicenseType license = svc.getCurrentLicense();
        ((LicenseCmdPrinter)cmdPrinter).setFeatureStr(feature);
        cmdPrinter.print(license, verbose);
        // Display :
        return null;
    }
}
