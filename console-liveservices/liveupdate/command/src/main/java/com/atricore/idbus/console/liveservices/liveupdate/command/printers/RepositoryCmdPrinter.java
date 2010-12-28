package com.atricore.idbus.console.liveservices.liveupdate.command.printers;

import com.atricore.idbus.console.liveservices.liveupdate.command.LiveUpdateCommandSupport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RepositoryCmdPrinter extends AbstractCmdPrinter<Repository> {

    public void print(LiveUpdateCommandSupport cmd, Repository r) {

        StringBuilder sb = new StringBuilder();

        sb.append("Repository'\n");
        sb.append(getNameValue("ID", r.getId()));
        sb.append(getNameValue("Name", r.getName()));
        sb.append(getNameValue("Enabled", r.isEnabled() + ""));
        sb.append(getNameValue("Location", r.getLocation().toString()));
        sb.append("\n");

        getOut().println(sb);
    }
}
