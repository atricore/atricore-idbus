package org.atricore.idbus.capabilities.spmlr2.command.printer;

import oasis.names.tc.spml._2._0.*;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Service
public class TargetPrinter extends AbstractCmdPrinter {

    public void printOutcome(Object response) {
        
        if (response instanceof ListTargetsResponseType) {
            printTargets(((ListTargetsResponseType)response).getTarget());
        } else {
            super.printResponse((ResponseType) response);
        }
    }
    
    protected void printTargets(List<TargetType> targets) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[0m  ID        Profile           Capabilities       \u001B[0m\n");
        for (TargetType target : targets) {

            // TODO : Build a line, using proper format and information (id, description, state, version, ... ?).
            // TODO : padd ids and states!
            sb.append("[");
            sb.append(getIdString(target));
            sb.append("]  [");
            sb.append(getProfileString(target));
            sb.append("]    [");
            sb.append(getCapabilitiesString(target));
            sb.append("]    ");

            sb.append("\n");

        }

        printMsg(sb);

    }

    protected String getIdString(TargetType target) {
        String id = target.getTargetID();
        if (id == null)
            id = "--";

        while (id.length() < 12) {
            id = " " + id;
        }

        return id;
    }

    protected String getProfileString(TargetType target) {
        String p = target.getProfile();
        if (p == null)
            p = "";

        while (p.length() < 4) {
            p = p + " ";
        }

        return p;
    }

    protected String getCapabilitiesString(TargetType target) {
        // TODO : Implement me
        if (target.getCapabilities() == null)
            return "--";

        CapabilitiesListType capabilitiesList = target.getCapabilities();

        List<CapabilityType>  capabilities = capabilitiesList.getCapability();
        if (capabilities == null)
            return "--";

        StringBuffer sb = new StringBuffer();

        for (CapabilityType capability : capabilities) {
            sb.append(capability.getNamespaceURI());
            sb.append(",");
        }

        return sb.toString();
    }
    
}
