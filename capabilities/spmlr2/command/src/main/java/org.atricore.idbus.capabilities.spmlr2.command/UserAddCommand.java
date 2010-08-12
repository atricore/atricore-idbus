package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
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
@Command(scope = "spml", name = "useradd", description = "SPML Add operation")
public class UserAddCommand extends SpmlCommandSupport {

    @Argument(index = 3, name = "targetId", description = "Provisionig Service Target id", required = true)
    String targetId;

    @Option(name = "-u", aliases = "--username", description = "Username ", required = true, multiValued = false)
    String username;

    @Option(name = "-p", aliases = "--password", description = "User Password", required = false, multiValued = false)
    String password;

    @Option(name = "-e", aliases = "--email", description = "User E-Mail", required = false, multiValued = false)
    String email;

    @Option(name = "-g", aliases = "--groups", description = "User Groups", required = false, multiValued = true)
    List<String> groupName = new ArrayList<String>();

    @Override
    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();

        AddRequestType req = new AddRequestType();
        req.setRequestID(idGen.generateId());
        req.setTargetID(targetId);

        // Use Atricore SPML schema ...
        UserType user = new UserType();
        user.setUsername(username);
        user.setUserPassword(password);
        user.setEmail(email);

        // Recover list of Groups
        List<GroupType> groups = retrieveGroups(psp, pspChannel, mediator);
        user.getGroup().addAll(groups);

        // TODO : Fill with user properties
        req.setData(user);

        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);
        AddResponseType res = (AddResponseType) mediator.sendMessage(req, ed, pspChannel);

        PSOType psoUser = res.getPso();
        PSOIdentifierType psoUserId = psoUser.getPsoID();

        System.out.println("Created user " + psoUserId.getID());

        return null;
    }

    protected List<GroupType> retrieveGroups(ProvisioningServiceProvider psp, PsPChannel pspChannel, SpmlR2PSPMediator mediator) {
        // TODO : Implement me!
        return new ArrayList<GroupType>();
    }
}
