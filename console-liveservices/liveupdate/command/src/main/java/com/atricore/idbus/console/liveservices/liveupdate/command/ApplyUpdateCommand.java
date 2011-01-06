package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "liveupdate", name = "apply-update", description = "Applies an available Update to the current setup")
public class ApplyUpdateCommand extends LiveUpdateCommandSupport {

    @Option(name = "-o", aliases = "--off-line", description = "Offline check for updates, use locally stored information", required = false, multiValued = false)
    boolean offline  = false;

    @Argument(name = "Update IU", description = "Install Unit fully qualified name (group/name/version)", index = 0, required = true)
    String fqKey;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        StringTokenizer st = new StringTokenizer(fqKey, "/", false);
        try {
            String group = st.nextToken();
            String name  = st.nextToken();
            String version = st.nextToken();

            svc.applyUpdate(group, name, version, offline);

        } catch (NoSuchElementException e) {
            throw new ParseException("Invalid Installable Unit FQN format in " + fqKey, 0);
        }

        return null;
    }
}
