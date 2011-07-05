package com.atricore.idbus.console.activation.command;

import com.atricore.idbus.console.activation.main.spi.ActivationService;
import com.atricore.idbus.console.activation._1_0.protocol.ActivateAgentRequestType;
import com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "activate", name = "josso-agent", description = "Activates a JOSSO Agent")
public class ActiavteExecEnvCommand extends ActivationCommandSupport {

    @Override
    protected Object doActivate(ActivationService svc) throws Exception {
        
        ActivateAgentRequestType request = new ActivateAgentRequestType();

        request.setForceInstall(forceInstall);
        request.setIdpHostName(idpHostName);
        request.setIdpPort(Integer.parseInt(idpPort));
        request.setIdpType(idpType);
        request.setJbossInstallDir(jbossInstallDir);
        request.setJbossInstance(jbossInstance);
        request.setPassword(password);
        request.setTarget(target);
        request.setTargetPlatformId(targetPlatformId);
        request.setTomcatInstallDir(tomcatInstallDir);
        request.setUser(user);
        request.setWeblogicDomain(weblogicDomain);

        return null; // TODO : svc.activateAgent(request);
    }
}
