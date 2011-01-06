package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "liveupdate", name = "clean-repo", description = "Clears local repository cache")
public class CleanRepositoryCommand extends LiveUpdateCommandSupport {

    @Argument(index = 0, name = "repository-id", description = "Repository identifier", required = true)
    String repositoryId;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        svc.cleanRepository(repositoryId);
        System.out.println("Repository cleared");
        return null;
    }
}
