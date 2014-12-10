package org.atricore.idbus.kernel.provisioning.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;

/**
 * Created by sgonzalez on 11/6/14.
 */
@Command(scope = "idm", name = "finduser", description = "IDM Find user command")
public class FindUserCommand extends ProvisioningCommandSupport {

    private static final Log logger = LogFactory.getLog(FindUserCommand.class);

    @Option(name = "-n", aliases = "--username", description = "Username", required = true, multiValued = false)
    String userName;

    @Override
    protected void doExecute(ProvisioningServiceProvider psp, ProvisioningTarget pst) {

        FindUserByUsernameRequest req = new FindUserByUsernameRequest();
        req.setUsername(userName);

        try {
            FindUserByUsernameResponse resp = pst.findUserByUsername(req);

            if (resp.getUser() != null)
                cmdPrinter.printUser(resp.getUser());
            else
                cmdPrinter.printErrMsg("User not found : " + userName);

        } catch (ProvisioningException e) {
            cmdPrinter.printErrMsg(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }
}
