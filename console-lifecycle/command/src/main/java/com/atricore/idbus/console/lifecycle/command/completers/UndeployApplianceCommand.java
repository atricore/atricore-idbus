package com.atricore.idbus.console.lifecycle.command.completers;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.UndeployIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.UndeployIdentityApplianceResponse;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "undeploy", description = "Undeploy Identity Appliance")
public class UndeployApplianceCommand extends ManagementCommandSupport {

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

        UndeployIdentityApplianceRequest req = new UndeployIdentityApplianceRequest();
        req.setApplianceId(id);
        UndeployIdentityApplianceResponse res = svc.undeployIdentityAppliance(req);
        return null;
    }

}
