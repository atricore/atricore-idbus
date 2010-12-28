package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * List available updates in a given repository
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "ls-repo-updates", description = "List available Updates in a given repository")
public class ListRepositoryUpdatesCommand extends LiveUpdateCommandSupport {

    @Argument(name = "repoName", description = "Repository Name", required = true, multiValued = false, index =  0)
    private String repoId;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        UpdatesIndexType updates = svc.getRepositoryUpdates(repoId);

        getPrinter().print(this, updates);

        return null;
    }
}
