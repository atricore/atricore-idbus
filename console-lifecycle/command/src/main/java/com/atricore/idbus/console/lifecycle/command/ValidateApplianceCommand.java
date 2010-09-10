package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ValidateApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ValidateApplianceResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "validate", description = "Verifies Identity Appliance integrity")
public class ValidateApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        ValidateApplianceRequest req = new ValidateApplianceRequest();
        req.setApplianceId(id);

        try {
            ValidateApplianceResponse res = svc.validateApplinace(req);
        } catch (ApplianceValidationException e) {
            System.out.println("Appliance " + id + " is not valid");
            cmdPrinter.printError(e);
            return null;
        }

        System.out.println("Appliance " + id + " is valid");

        return null;
    }
}
