package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityAppliancesRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ListIdentityAppliancesResponse;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "list", description = "List defined identity appliances")
public class ListAppliancesCommand extends ManagementCommandSupport {

    @Option(name = "-s", aliases = "--state", description = "List appliances in the specified states", required = false, multiValued = true)
    List<String> states;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        // TODO : Implement list by state

        ListIdentityAppliancesRequest req = new ListIdentityAppliancesRequest ();
        ListIdentityAppliancesResponse res  = svc.listIdentityAppliances(req);

        cmdPrinter.printAll(res.getIdentityAppliances());

        return null;
    }

}
