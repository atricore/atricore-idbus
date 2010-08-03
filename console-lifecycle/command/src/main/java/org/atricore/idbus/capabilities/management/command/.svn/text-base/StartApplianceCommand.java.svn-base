package org.atricore.idbus.capabilities.management.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceLifeCycleAction;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.ManageIdentityApplianceLifeCycleRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.ManageIdentityApplianceLifeCycleResponse;

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
