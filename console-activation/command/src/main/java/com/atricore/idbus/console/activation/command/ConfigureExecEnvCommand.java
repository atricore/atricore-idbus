package com.atricore.idbus.console.activation.command;

import com.atricore.idbus.console.activation.main.spi.ActivationService;
import com.atricore.idbus.console.activation.main.spi.request.ConfigureAgentRequest;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ConfigureExecEnvCommand extends ActivationCommandSupport {

    @Override
    protected Object doActivate(ActivationService svc) throws Exception {
        ConfigureAgentRequest request = new ConfigureAgentRequest();

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

        request.setReplaceConfig(replaceConfig);

        return svc.configureAgent(request);
    }
}
