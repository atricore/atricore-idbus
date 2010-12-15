package com.atricore.idbus.console.activation.command;

import com.atricore.idbus.console.activation.main.spi.ActivationService;
import com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "activate", name = "samples", description = "Activates a Sample Partner Appliaciont")
public class ActivateSamplesCommand extends ActivationCommandSupport {
    @Override
    protected Object doActivate(ActivationService svc) throws Exception {

        ActivateSamplesRequest request = new ActivateSamplesRequest();

        request.setForceInstall(forceInstall);
        request.setIdpHostName(idpHostName);
        request.setIdpPort(idpPort);
        request.setIdpType(idpType);
        request.setJbossInstallDir(jbossInstallDir);
        request.setJbossInstance(jbossInstance);
        request.setPassword(password);
        request.setTarget(target);
        request.setTargetPlatformId(targetPlatformId);
        request.setTomcatInstallDir(tomcatInstallDir);
        request.setUser(user);
        request.setWeblogicDomain(weblogicDomain);

        return svc.activateSamples(request);
    }
}
