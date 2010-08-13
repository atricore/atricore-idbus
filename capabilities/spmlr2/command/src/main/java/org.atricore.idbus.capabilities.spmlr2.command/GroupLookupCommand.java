package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.LookupRequestType;
import oasis.names.tc.spml._2._0.LookupResponseType;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.RequestType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "grplookup", description = "SPML Group Lookup operation")
public class GroupLookupCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "Group ID", required = false, multiValued = false)
    Long id;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) {
        PSOIdentifierType psoGroupId = new PSOIdentifierType();
        psoGroupId.setTargetID(targetId);
        psoGroupId.setID(id + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
        spmlRequest.setPsoID(psoGroupId);

        return spmlRequest;
    }
}
