package org.atricore.idbus.capabilities.spmlr2.command;

import org.apache.felix.gogo.commands.Command;
import org.atricore.idbus.capabilities.spmlr2.main.common.AbstractSpmlR2Mediator;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTargetManager;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "ls-targets", description = "List Provisioning Targets")
public class ListTargetsCommand extends SmplCommandSupport {


    @Override
    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {
        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();

        List<ProvisioningTargetManager> targets = mediator.getProvisioningTargets();

        if (targets == null || targets.size() == 0)
            throw new Exception("No targets found in PSP " + psp.getName());

        StringBuilder sb = new StringBuilder();

        // Build headers line

        sb.append("  ID        Name           Description\n");

        for (ProvisioningTargetManager target : targets) {

            IdentityPartition partition = target.getIdentityPartition();

            if (partition == null) {
                sb.append(psp.getName()).append(" has NO identity partition !\n");
                continue;
            }
            // System out ?

            // TODO : Build a line, using proper format and information (id, description, state, version, ... ?).
            // TODO : padd ids and states!
            sb.append("[");
            sb.append(getIdString(partition));
            sb.append("]  [");
            sb.append(getNameString(partition));
            sb.append("]    [");
            sb.append(partition.getDescription());
            sb.append("]    ");


            sb.append("\n");

        }

        System.out.println(sb);

        return null;
    }

    protected String getIdString(IdentityPartition partition) {
        String id = partition.getId() + "";

        while (id.length() < 4) {
            id = " " + id;
        }

        return id;
    }

    protected String getNameString(IdentityPartition partition) {
        String name = partition.getName();
        if (name == null)
            name = "<NOT-DEFINED>";

        while (name.length() < 16) {
            name = name + " ";
        }

        return name;
    }


}
