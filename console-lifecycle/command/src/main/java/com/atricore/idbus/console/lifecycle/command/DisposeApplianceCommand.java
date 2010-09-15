package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceLifeCycleAction;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.DisposeIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.DisposeIdentityApplianceResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "dispose", description = "Dispose Identity Appliance")
public class DisposeApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {
        DisposeIdentityApplianceRequest req = new DisposeIdentityApplianceRequest();
        req.setId(id);
        DisposeIdentityApplianceResponse res = svc.disposeIdentityAppliance(req);

        return null;
    }
}
