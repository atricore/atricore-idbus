package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;

/**
 * List Available updates for the current setup.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "list-updates", description = "List available Updates for the current setup")
public class ListUpdatesCommand extends LiveUpdateCommandSupport {

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        Collection<UpdateDescriptorType> updates = svc.getAvailableUpdates();
        getPrinter().printAll(updates);
        return null;
    }
}
