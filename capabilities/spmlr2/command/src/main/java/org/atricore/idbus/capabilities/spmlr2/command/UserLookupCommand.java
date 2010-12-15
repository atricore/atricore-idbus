package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.LookupRequestType;
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
@Command(scope = "spml", name = "usrlookup", description = "SPML User LOOKUP operation")
public class UserLookupCommand extends SpmlCommandSupport {
    
    @Option(name = "-i", aliases = "--id", description = "User ID", required = false, multiValued = false)
    Long id;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) {
        PSOIdentifierType psoUserId = new PSOIdentifierType();
        psoUserId.setTargetID(targetId);
        psoUserId.setID(id + "");
        psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
        spmlRequest.setPsoID(psoUserId);

        return spmlRequest;
    }
    
}
