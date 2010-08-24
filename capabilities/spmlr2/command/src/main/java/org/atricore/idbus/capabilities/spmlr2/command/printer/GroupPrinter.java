package org.atricore.idbus.capabilities.spmlr2.command.printer;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class GroupPrinter extends AbstractCmdPrinter {
    
    public void printOutcome(Object spmlResponse) {
        if (spmlResponse instanceof LookupResponseType) {
            LookupResponseType lkRes = (LookupResponseType) spmlResponse;
            printGroup(lkRes.getPso());
        } else if (spmlResponse instanceof SearchResponseType) {
            SearchResponseType schRes = (SearchResponseType) spmlResponse;
            printGroups(schRes.getPso());
        } else if (spmlResponse instanceof AddResponseType) {
            AddResponseType addRes = (AddResponseType) spmlResponse;
            printGroup(addRes.getPso());
        } else if (spmlResponse instanceof ModifyResponseType) {
            ModifyResponseType modRes = (ModifyResponseType) spmlResponse;
            printGroup(modRes.getPso());
        } else {
            super.printResponse((ResponseType) spmlResponse);
        }
            
    }
    
    
    public void printGroups(List<PSOType> psoGroups) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("  ID                Name                     Description       \n");

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
