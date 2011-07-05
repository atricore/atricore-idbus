package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.DisposeIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.RemoveIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.DisposeIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.RemoveIdentityApplianceResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "remove", description = "Dispose Identity Appliance")
public class RemoveApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {
        RemoveIdentityApplianceRequest req = new RemoveIdentityApplianceRequest();
        req.setApplianceId(id);
        RemoveIdentityApplianceResponse res = svc.removeIdentityAppliance(req);

        return null;
    }
}
