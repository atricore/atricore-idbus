package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ActivateSPExecEnvRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ActivateSPExecEnvResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "activate-sp", description = "Activate SP execution environment")
public class ActivateSPExecEnvCommand extends ManagementCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Argument(index = 1, name = "sp", description = "Service Provider name", required = true, multiValued = false)
    String sp;

    @Option(name = "-f", aliases = "--force", description = "Force activation", required = false, multiValued = false)
    boolean force = false;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        if (verbose)
            System.out.println("Activating SP Execution Environment for " + sp + " in appliance " + id);

        ActivateSPExecEnvRequest req = new ActivateSPExecEnvRequest();
        req.setApplianceId(id);
        req.setSPName(sp);
        req.setReactivate(force);
        ActivateSPExecEnvResponse res = svc.activateSPExecEnv(req);

        return null;


    }
}
