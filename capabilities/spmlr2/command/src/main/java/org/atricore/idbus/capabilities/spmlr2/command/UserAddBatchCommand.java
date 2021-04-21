package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.AddRequestType;
import oasis.names.tc.spml._2._0.PSOType;
import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.ResponseType;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgonzalez on 4/24/14.
 */
public class UserAddBatchCommand extends SpmlCommandSupport {

    //<--- General Information ---->
    @Option(name = "-u", aliases = "--username", description = "Username prefix", required = true, multiValued = false)
    String userName;

    @Option(name = "-n", aliases = "--name", description = "User first name prefix ", required = false, multiValued = false)
    String firstName;

    @Option(name = "-s", aliases = "--surename", description = "User last name prefix", required = false, multiValued = false)
    String surename;

    @Option(name = "-e", aliases = "--email", description = "User e-mail suffix", required = false, multiValued = false)
    String email;

    @Option(name = "-f", aliases = "--from", description = "Index from", required = false, multiValued = false)
    Integer from;

    @Option(name = "-t", aliases = "--to", description = "Index to", required = false, multiValued = false)
    Integer to;

    @Option(name = "-g", aliases = "--group", description = "User group names", required = false, multiValued = true)
    List<String> groupName = new ArrayList<String>();

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {
        throw new UnsupportedOperationException("Not supported in batch mode");
    }

    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        assert to > from : "Invalid arguments to/from";

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();
        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);

        for (int usrIndex = from ; usrIndex < to ; usrIndex++) {

            RequestType spmlRequest = buildSpmlRequest(psp, pspChannel, usrIndex);

            if (verbose)
                cmdPrinter.printMsg("SPML Endpoint " + ed.getLocation());

            Object o = mediator.sendMessage(spmlRequest, ed, pspChannel);

            if (o instanceof ResponseType) {
                ResponseType spmlResponse = (ResponseType) o;

                if (verbose)
                    cmdPrinter.printRequest(spmlRequest);

                if (verbose)
                    cmdPrinter.printResponse(spmlResponse);

                cmdPrinter.printOutcome(spmlResponse);

            } else {
                cmdPrinter.printErrMsg("Unexpected message received, command execution error. Type 'log:display-exception' for details");
            }
        }


        return null;

    }


    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel, int userIndex) throws Exception {
        AddRequestType req = new AddRequestType();
        req.setRequestID(uuidGenerator.generateId());
        req.setTargetID(targetId);

        // Use Atricore SPML schema ...
        UserType spmlUser = new UserType();

        // Fill with user properties
        spmlUser.setUserName(userName + userIndex);
        spmlUser.setUserPassword("user" + userIndex + "pwd");

        if (email != null)
            spmlUser.setEmail("user" +  + userIndex + "@" + email);

        if (firstName  != null)
            spmlUser.setFirstName(firstName + userIndex);

        if (surename  != null)
            spmlUser.setSurename(surename + userIndex);

        // Recover list of Groups

        if (this.groupName != null) {
            spmlUser.getGroup().clear();
            for (String groupName : this.groupName) {
                PSOType psoGroup = lookupGroup(pspChannel, groupName);
                GroupType spmlGroup = (GroupType) psoGroup.getData();
                spmlUser.getGroup().add(spmlGroup);
            }
        }

        req.setData(spmlUser);
        req.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        return req;
    }
}
