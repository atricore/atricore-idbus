package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.LookupIdentityApplianceByIdResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "view", description = "Activate Execution Environment")
public class ViewApplianceDetailsCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id if the identity appliance", required = true, multiValued = false)
    String id;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        LookupIdentityApplianceByIdRequest req = new LookupIdentityApplianceByIdRequest ();
        req.setIdentityApplianceId(id);
        LookupIdentityApplianceByIdResponse res = svc.lookupIdentityApplianceById(req);

        cmdPrinter.print(res.getIdentityAppliance());

        return null;

    }
}
