package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "profile", description = "View configured profile details")
public class CurrentProfileCommand extends LiveUpdateCommandSupport {

    @Option(name = "-r", aliases = "--rebuild", description ="Forces a profile recalculation", required = false, multiValued = false)
    boolean rebuild = false;

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        ProfileType p = svc.getCurrentProfile(rebuild);
        getPrinter().print(this, p);
        return null;
    }
}
