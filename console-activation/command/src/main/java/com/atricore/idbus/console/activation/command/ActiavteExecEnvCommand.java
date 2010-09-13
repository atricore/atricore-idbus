package com.atricore.idbus.console.activation.command;

import com.atricore.idbus.console.activation.main.spi.ActivationService;
import com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "activate", name = "josso-agent", description = "Activates a JOSSO Agent")
public class ActiavteExecEnvCommand extends ActivationCommandSupport {

    @Override
    protected Object doActivate(ActivationService svc) throws Exception {
        
        ActivateAgentRequest request = new ActivateAgentRequest();

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

        return svc.activateAgent(request);
    }
}
