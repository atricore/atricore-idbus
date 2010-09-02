package org.atricore.idbus.capabilities.spmlr2.command.printer;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserPrinter extends AbstractCmdPrinter {
    
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
            printUser(lkRes.getPso());
        } else if (response instanceof SearchResponseType) {
            SearchResponseType schRes = (SearchResponseType) response;
            printUsers(schRes.getPso());
        } else if (response instanceof AddResponseType) {
            AddResponseType addRes = (AddResponseType) response;
            printUser(addRes.getPso());
        } else if (response instanceof ModifyResponseType) {
            ModifyResponseType modRes = (ModifyResponseType) response;
            printUser(modRes.getPso());
        } else {
            super.printResponse((ResponseType) response);
        }
            
    }
    
    public void printUsers(List<PSOType> psoUsers) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("  ID                Name                     Description       \n");

        for (PSOType psoUser : psoUsers) {
            psoUser.getPsoID();
            UserType spmlUser = (UserType) psoUser.getData();
            sb.append("[");
            sb.append(getPsoIDString(psoUser.getPsoID()));
            sb.append("]  [");
            sb.append(getUserNameString(spmlUser));
            sb.append("]    [");
            sb.append(getUserEmailString(spmlUser));
            sb.append("]    [");
            sb.append(getUserGroupsString(spmlUser));
            sb.append("]    ");

            sb.append("\n");
        }

        printMsg(sb);
    }

    public void printUser(PSOType psoUser) {

        StringBuilder sb = new StringBuilder();

        UserType spmlUser = (UserType) psoUser.getData();
        PSOIdentifierType psoGroupId = psoUser.getPsoID();

        sb.append(getLabelString("ID"));
        sb.append(getPsoIDString(psoGroupId));
        sb.append("\n");

        sb.append(getLabelString("Name"));
        sb.append(getUserNameString(spmlUser));
        sb.append("\n");

        sb.append(getLabelString("E-Mail"));
        sb.append(getUserEmailString(spmlUser));
        sb.append("\n");
        
        sb.append(getLabelString("Groups"));
        sb.append(getUserGroupsString(spmlUser));
        sb.append("\n");
        

        printMsg(sb);

    }
    
    
    protected String getUserNameString(UserType spmlUser) {
        return getLeftString(spmlUser.getUserName(), 12);
        
    }
    
    protected String getUserEmailString(UserType spmlUser) {
        return getLeftString(spmlUser.getEmail(), 18);
    }

    protected String getUserGroupsString(UserType spmlUser) {

        String groups = "";
        String prefix = "";

        for (GroupType spmlGroup : spmlUser.getGroup()) {
            groups += prefix + spmlGroup.getName();
            prefix = ", ";
        }
        return getLeftString(groups, 64);
    }
    
    
    
}
