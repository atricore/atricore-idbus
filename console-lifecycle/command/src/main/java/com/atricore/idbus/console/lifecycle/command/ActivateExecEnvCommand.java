package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ActivateExecEnvRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ActivateExecEnvResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "activate", description = "Activate Execution Environment")
public class ActivateExecEnvCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Argument(index = 1, name = "exec-env", description = "Execution environment name", required = true, multiValued = false)
    String execEnv;

    @Option(name = "-f", aliases = "--force", description = "Force activation", required = false, multiValued = false)
    boolean force = false;

    @Option(name = "-r", aliases = "--replace", description = "Replace configuration files", required = false, multiValued = false)
    boolean replace = false;

    @Option(name = "-s", aliases = "--samples", description = "Activate sample partner application", required = false, multiValued = false)
    boolean samples = false;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        if (verbose)
            System.out.println("Activating Execution Environment " + execEnv + " in appliance " + id);

        ActivateExecEnvRequest req = new ActivateExecEnvRequest();
        req.setApplianceId(id);
        req.setExecEnvName(execEnv);
        req.setReactivate(force);
        req.setReplace(replace);
        req.setActivateSamples(samples);
        ActivateExecEnvResponse res = svc.activateExecEnv(req);

        return null;


    }
}
