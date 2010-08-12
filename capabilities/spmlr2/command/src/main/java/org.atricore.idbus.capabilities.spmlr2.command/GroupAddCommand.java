package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.AddRequestType;
import oasis.names.tc.spml._2._0.AddResponseType;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.PSOType;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "groupadd", description = "SPML Group Add operation")
public class GroupAddCommand extends SpmlCommandSupport {

    @Argument(index = 2, name = "targetId", description = "Provisionig Service Target id", required = true)
    String targetId;

    @Option(name = "-n", aliases = "--name", description = "Group Name", required = true, multiValued = false)
    String name;

    @Option(name = "-d", aliases = "--description", description = "Group Description", required = true, multiValued = false)
    String description;

    @Override
    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();

        AddRequestType req = new AddRequestType();
        req.setRequestID(idGen.generateId());
        req.setTargetID(targetId);

        // Use Atricore SPML schema ...
        GroupType group = new GroupType ();
        group.setName(name);
        group.setDescription(description);

        // TODO : Fill with user properties
        req.setData(group);

        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);
        AddResponseType res = (AddResponseType) mediator.sendMessage(req, ed, pspChannel);

        PSOType psoUser = res.getPso();
        PSOIdentifierType psoUserId = psoUser.getPsoID();

        System.out.println("Created group " + psoUserId.getID());

        return null;
    }

    protected List<GroupType> retrieveGroups(ProvisioningServiceProvider psp, PsPChannel pspChannel, SpmlR2PSPMediator mediator) {
        // TODO : Implement me!
        return new ArrayList<GroupType>();
    }
}
