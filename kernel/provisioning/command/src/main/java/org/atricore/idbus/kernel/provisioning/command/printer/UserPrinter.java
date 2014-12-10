package org.atricore.idbus.kernel.provisioning.command.printer;

import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.util.List;

/**
 * Created by sgonzalez on 11/6/14.
 */
public class UserPrinter extends AbstractCmdPrinter {

    public void printUsers(List<User> users) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[1m  ID                Username         E-Mail               Groups\u001B[0m\n");

        for (User user : users) {

            sb.append("[");
            sb.append(user.getOid());
            sb.append("]  [");
            sb.append(getUserNameString(user));
            sb.append("]    [");
            sb.append(getUserEmailString(user));
            sb.append("]    [");
            sb.append(getUserGroupsString(user));
            sb.append("]    ");

            sb.append("\n");
        }

        printMsg(sb);
    }

    public void printUser(User user) {

        StringBuilder sb = new StringBuilder();

        sb.append("\u001B[1m  User details : \u001B[0m\n");
        sb.append("----------------\n");

        sb.append(getLabelString("OID"));
        sb.append(user.getOid() != null ? user.getOid() : "N/A");
        sb.append("\n");

        sb.append(getLabelString("ID"));
        sb.append(user.getId());
        sb.append("\n");

        sb.append(getLabelString("Username"));
        sb.append(getUserNameString(user));
        sb.append("\n");

        sb.append(getLabelString("E-Mail"));
        sb.append(getUserEmailString(user));
        sb.append("\n");

        sb.append(getLabelString("GivenName"));
        sb.append(getLeftString(user.getUserName(), 30));
        sb.append("\n");

        sb.append(getLabelString("FamillyName"));
        sb.append(getLeftString(user.getSurename(), 30));
        sb.append("\n");

        sb.append(getLabelString("Title"));
        sb.append(getLeftString(user.getPersonalTitle(), 10));
        sb.append("\n");

        sb.append(getLabelString("Organization"));
        sb.append(getLeftString(user.getOrganizationName(), 30));
        sb.append("\n");

        sb.append(getLabelString("OrgUnit"));
        sb.append(getLeftString(user.getOrganizationUnitName(), 30));
        sb.append("\n");

        sb.append(getLabelString("Groups"));
        sb.append(getUserGroupsString(user));
        sb.append("\n");

        // TODO : Display more attributes!

        printMsg(sb);

    }


    protected String getUserNameString(User user) {
        return getLeftString(user.getUserName(), 12);
    }

    protected String getUserEmailString(User user) {
        return getLeftString(user.getEmail(), 18);
    }

    protected String getUserGroupsString(User user) {

        String groups = "";
        String prefix = "";

        if (user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                groups += prefix + group.getName();
                prefix = ", ";
            }
            return getLeftString(groups, 64);
        }

        return "";
    }
}
