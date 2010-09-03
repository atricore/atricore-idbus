package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceLifeCycleAction;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ManageIdentityApplianceLifeCycleRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ManageIdentityApplianceLifeCycleResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "start", description = "Start Identity Appliance")
public class StartApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {
        ManageIdentityApplianceLifeCycleRequest req = new ManageIdentityApplianceLifeCycleRequest();
        req.setApplianceId(id);
        req.setAction(IdentityApplianceLifeCycleAction.START);
        ManageIdentityApplianceLifeCycleResponse res = svc.manageIdentityApplianceLifeCycle(req);
        return null;
    }
}
