package com.atricore.idbus.console.liveservices.liveupdate.command.printers;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RepositoryCmdPrinter extends AbstractCmdPrinter<Repository> {

    public void print(Repository o) {

    }

    public void printAll(Collection<Repository> os) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[1m  Name           URI\u001B[0m\n");
        sb.append("                                   Last/Deployed\n");

        for (Repository r : os) {
            sb.append("[");
            sb.append(getNameString(r.getId(), 16));
            sb.append("]  [");
            sb.append(getNameString(r.getName()));
            sb.append("]  [");
            sb.append(getEnabledString(r.isEnabled()));
            sb.append("]  [");
            sb.append(getNameString(r.getLocation().toString(), 64));
            sb.append("]");
            sb.append("\n");
        }

        getOut().println(sb);
    }
}
