package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "list-repos", description = "List configured repositories")
public class ListRepositoriesCommand extends LiveUpdateCommandSupport {

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        Collection<Repository> repos = svc.getRepositories();
        this.getPrinter().printAll(this, repos);
        return null;
    }
}
