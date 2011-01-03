package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.util.Collection;

/**
 * List Available updates for the current setup.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "ls-updates", description = "List available Updates for the current setup")
public class ListUpdatesCommand extends LiveUpdateCommandSupport {

    @Option(name = "-o", aliases = "--off-line", description = "Offline check for updates, use locally stored information", required = false, multiValued = false)
    boolean offline  = false;

    @Argument(name = "Installed IU", description = "Installable Unit fully qualified name (group/name/version)", index = 0, required = false)
    String fqKey;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        Collection<UpdateDescriptorType> updates =
                fqKey != null ?
                    (offline ? svc.getAvailableUpdates(fqKey) : svc.checkForUpdates(fqKey)) :
                    (offline ? svc.getAvailableUpdates() : svc.checkForUpdates());

        getPrinter().printAll(this, updates);
        return null;
    }
}
