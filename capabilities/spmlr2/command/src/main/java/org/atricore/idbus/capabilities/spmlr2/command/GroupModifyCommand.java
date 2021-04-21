package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "grpmodify", description = "SPML Group MODIFY operation")
public class GroupModifyCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "Group ID", required = true, multiValued = false)
    String id;

    @Option(name = "-n", aliases = "--name", description = "Group name", required = false, multiValued = false)
    String name;

    @Option(name = "-d", aliases = "--description", description = "Group description", required = false, multiValued = false)
    String description;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {
        ModifyRequestType spmlRequest = new ModifyRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        GroupType spmlGroup = new GroupType();
        spmlGroup.setId(id);
        if (name != null)
            spmlGroup.setName(name);

        if (description != null)
            spmlGroup.setDescription(description);

        PSOIdentifierType psoId = new PSOIdentifierType();
        psoId.setID(id + "");
        psoId.setTargetID(targetId);

        PSOType psoGroup = new PSOType();
        psoGroup.setPsoID(psoId);
        psoGroup.setData(spmlGroup);

        ModificationType mod = new ModificationType();

        mod.setModificationMode(ModificationModeType.REPLACE);
        mod.setData(spmlGroup);

        spmlRequest.setPsoID(psoGroup.getPsoID());
        spmlRequest.getModification().add(mod);

        return spmlRequest;
    }

}
