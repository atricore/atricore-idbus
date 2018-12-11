package org.atricore.idbus.capabilities.spmlr2.command.printer;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Service
public class GroupPrinter extends AbstractCmdPrinter {
    
    public void printOutcome(Object response) {

        if (response instanceof ResponseType ) {
            ResponseType spmlResponse = (ResponseType) response;

            if (!spmlResponse.getStatus().equals(StatusCodeType.SUCCESS)) {
                super.printOutcome(spmlResponse);
                return;
            }
        }
        
        if (response instanceof LookupResponseType) {
            LookupResponseType lkRes = (LookupResponseType) response;
            printGroup(lkRes.getPso());
        } else if (response instanceof SearchResponseType) {
            SearchResponseType schRes = (SearchResponseType) response;
            printGroups(schRes.getPso());
        } else if (response instanceof AddResponseType) {
            AddResponseType addRes = (AddResponseType) response;
            printGroup(addRes.getPso());
        } else if (response instanceof ModifyResponseType) {
            ModifyResponseType modRes = (ModifyResponseType) response;
            printGroup(modRes.getPso());
        } else {
            super.printResponse((ResponseType) response);
        }
            
    }
    
    
    public void printGroups(List<PSOType> psoGroups) {

        StringBuilder sb = new StringBuilder();
        // Build headers line

        sb.append("\u001B[1m  ID                Name                     Description       \u001B[0m\n");

        for (PSOType psoGroup : psoGroups) {
            psoGroup.getPsoID();
            GroupType spmlGroup = (GroupType) psoGroup.getData();
            sb.append("[");
            sb.append(getPsoIDString(psoGroup.getPsoID()));
            sb.append("]  [");
            sb.append(getGroupNameString(spmlGroup));
            sb.append("]    [");
            sb.append(getGroupDescriptionString(spmlGroup));
            sb.append("]    ");

            sb.append("\n");
        }

        printMsg(sb);
    }

    public void printGroup(PSOType psoGroup) {

        StringBuilder sb = new StringBuilder();

        GroupType spmlGroup = (GroupType) psoGroup.getData();
        PSOIdentifierType psoGroupId = psoGroup.getPsoID();

        sb.append(getLabelString("ID"));
        sb.append(getPsoIDString(psoGroupId));
        sb.append("\n");

        sb.append(getLabelString("Name"));
        sb.append(getGroupNameString(spmlGroup));
        sb.append("\n");

        sb.append(getLabelString("Description"));
        sb.append(getGroupDescriptionString(spmlGroup));
        sb.append("\n");

        printMsg(sb);

    }

    protected String getGroupNameString(GroupType spmlGroup) {
        return getLeftString(spmlGroup.getName(), 12);
    }

    protected String getGroupDescriptionString(GroupType spmlGroup) {
        return getLeftString(spmlGroup.getDescription(), 64);
    }

}
