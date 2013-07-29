package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.UndisposeIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.UndisposeIdentityApplianceResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "undispose", description = "Un-dispose Identity Appliance")
public class UndisposeApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {
        UndisposeIdentityApplianceRequest req = new UndisposeIdentityApplianceRequest();
        req.setId(id);
        UndisposeIdentityApplianceResponse res = svc.undisposeIdentityAppliance(req);

        return null;
    }
}

