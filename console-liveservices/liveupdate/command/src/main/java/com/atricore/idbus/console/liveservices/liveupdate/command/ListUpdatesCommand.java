package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.text.ParseException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * List Available updates for the given install unit.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "updates", description = "List available Updates for the current setup")
public class ListUpdatesCommand extends LiveUpdateCommandSupport {

    @Option(name = "-o", aliases = "--off-line", description = "Offline check for updates, use locally stored information", required = false, multiValued = false)
    boolean offline  = false;

    @Argument(name = "Updatable IU", description = "Install Unit fully qualified name (group/name/version)", index = 0, required = false)
    String fqKey;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {

        Collection<com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType> updates = null;

        if (fqKey != null) {

            StringTokenizer st = new StringTokenizer(fqKey, "/", false);
            try {
                String group = st.nextToken();
                String name  = st.nextToken();
                String version = st.nextToken();

                updates = offline ? svc.getAvailableUpdates(group, name, version) : svc.checkForUpdates(group, name, version);
            } catch (NoSuchElementException e) {
                throw new ParseException("Invalid Installable Unit FQN format in " + fqKey, 0);
            }
        } else {
            updates = offline ? svc.getAvailableUpdates() : svc.checkForUpdates();
        }


        getPrinter().printAll(this, updates);

        return null;

    }

}
