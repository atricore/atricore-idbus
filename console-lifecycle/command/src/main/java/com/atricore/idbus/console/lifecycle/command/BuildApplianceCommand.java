package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.BuildIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.BuildIdentityApplianceResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "build", description = "Build Identity Appliance from definition")
public class BuildApplianceCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id if the identity appliance", required = true, multiValued = false)
    String id;

    @Option(name = "-d", aliases = "--deploy", description = "Deploy and start the Identity Appliance ", required = false, multiValued = false)
    boolean deploy = false;

    @Option(name = "-v", aliases = "--verbose", description = "Print out additional information during deployment", required = false, multiValued = false)
    boolean verbose = false;


    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        // This will trigger identity appliance build service!
        BuildIdentityApplianceRequest req = new BuildIdentityApplianceRequest(id, deploy);

        if (verbose)
            System.out.println("Building Identity Appliance " + id + "... This may take several minutes!");

        BuildIdentityApplianceResponse res = svc.buildIdentityAppliance(req);

        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }
}
