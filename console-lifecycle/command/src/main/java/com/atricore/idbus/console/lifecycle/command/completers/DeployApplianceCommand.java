package com.atricore.idbus.console.lifecycle.command.completers;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.DeployIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.DeployIdentityApplianceResponse;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "deploy", description = "Deploy Identity Appliance")
public class DeployApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Option(name = "-s", aliases = "--start", description = "Start the Identity Appliance ", required = false, multiValued = false)
    boolean start = false;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        DeployIdentityApplianceRequest req = new DeployIdentityApplianceRequest ();
        req.setApplianceId(id);
        req.setStartAppliance(start);
        DeployIdentityApplianceResponse res = svc.deployIdentityAppliance(req);

        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
