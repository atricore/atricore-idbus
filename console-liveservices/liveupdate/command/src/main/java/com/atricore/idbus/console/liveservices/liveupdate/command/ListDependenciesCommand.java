package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "liveupdate", name = "ls-dependencies", description = "List dependencies required by a given update," +
        " so that it can be installed in current setup.")
public class ListDependenciesCommand extends LiveUpdateCommandSupport {

    @Argument(name = "update", description = "Installable Unit fully qualified name (group/name/version)", index = 0, required = true)
    String fqKey;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        StringTokenizer st = new StringTokenizer(fqKey, "/", false);

        try {
            String group = st.nextToken();
            String name  = st.nextToken();
            String version = st.nextToken();

            ProfileType p = svc.getUpdateProfile(group, name, version);
            getPrinter().print(this, p);
            return null;
        } catch (NoSuchElementException e) {
            throw new ParseException("Invalid update name format in " + fqKey, 0);
        }
    }
}
