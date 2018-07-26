package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.DeleteRequestType;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.RequestType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "grpdelete", description = "SPML Group DELETE operation")
public class GroupDeleteCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "Group ID", required = true, multiValued = false)
    Long id;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {
        DeleteRequestType spmlRequest = new DeleteRequestType ();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        PSOIdentifierType psoId = new PSOIdentifierType ();
        psoId.setID(id + "");
        psoId.setTargetID(targetId);

        spmlRequest.setPsoID(psoId);
        
        return spmlRequest;


    }
}
