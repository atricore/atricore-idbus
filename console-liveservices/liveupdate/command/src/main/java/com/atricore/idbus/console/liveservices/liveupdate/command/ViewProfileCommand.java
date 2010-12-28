package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate", name = "profile", description = "View configured profile details")
public class ViewProfileCommand extends LiveUpdateCommandSupport {

    @Override
    protected Object doExecute(LiveUpdateManager svc) throws Exception {
        ProfileType p = svc.getCurrentProfile();
        getPrinter().print(this, p);
        return null;
    }
}
