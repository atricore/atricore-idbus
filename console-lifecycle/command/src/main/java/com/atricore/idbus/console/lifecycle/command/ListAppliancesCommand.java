package com.atricore.idbus.console.lifecycle.command;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityAppliancesRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ListIdentityAppliancesResponse;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "list", description = "List defined identity appliances")
public class ListAppliancesCommand extends ManagementCommandSupport {

    @Option(name = "-s", aliases = "--state", description = "List appliances for the specified states", required = false, multiValued = true)
    List<String> states;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        // TODO : Implement list by state

        ListIdentityAppliancesRequest req = new ListIdentityAppliancesRequest ();
        ListIdentityAppliancesResponse res  = svc.listIdentityAppliances(req);

        StringBuilder sb = new StringBuilder();

        // Build headers line

        sb.append("  ID        State       Revision    Description\n");
        sb.append("                      Last/Deployed\n");

        for (IdentityAppliance appliance : res.getIdentityAppliances()) {
            // System out ?

            // TODO : Build a line, using proper format and information (id, description, state, version, ... ?).
            // TODO : padd ids and states!
            sb.append("[");
            sb.append(getIdString(appliance));
            sb.append("]  [");
            sb.append(getStateString(appliance));
            sb.append("]    [");
            sb.append(getRevisionString(appliance.getIdApplianceDefinition().getRevision()));
            sb.append("/");
            if (appliance.getIdApplianceDeployment() != null) {
                sb.append(getRevisionString(appliance.getIdApplianceDeployment().getDeployedRevision()));
            } else {
                sb.append("  -");
            }
            sb.append("]    ");
            sb.append(appliance.getIdApplianceDefinition().getDescription());

            sb.append("\n");

        }

        System.out.println(sb);

        return null;
    }

    protected String getStateString(IdentityAppliance appliance) {

        String state = appliance.getState();
        while (state.length() < 10) {
            state = state + " ";
        }
        return state;

    }

    protected String getIdString(IdentityAppliance appliance) {
        String id = appliance.getId() + "";

        while (id.length() < 4) {
            id = " " + id;
        }

        return id;
    }

    protected String getRevisionString(int revision) {

        if (revision > 99)
            return "" + revision;

        if (revision > 9)
            return "0" + revision;

        return "00" + revision;

    }
}
