package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
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
@Command(scope = "spml", name = "grpadd", description = "SPML Group ADD operation")
public class GroupAddCommand extends SpmlCommandSupport {

    @Option(name = "-n", aliases = "--name", description = "Group name", required = true, multiValued = false)
    String name;

    @Option(name = "-d", aliases = "--description", description = "Group description", required = true, multiValued = false)
    String description;

    protected List<GroupType> retrieveGroups(ProvisioningServiceProvider psp, PsPChannel pspChannel, SpmlR2PSPMediator mediator) {
        // TODO : Implement me!
        return new ArrayList<GroupType>();
    }

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) {
        AddRequestType req = new AddRequestType();
        req.setRequestID(uuidGenerator.generateId());
        req.setTargetID(targetId);

        // Use Atricore SPML schema ...
        GroupType group = new GroupType ();
        group.setName(name);
        group.setDescription(description);

        // TODO : Fill with user properties
        req.setData(group);

        return req;
    }
}
