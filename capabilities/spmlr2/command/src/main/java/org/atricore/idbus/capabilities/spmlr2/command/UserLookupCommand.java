package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.LookupRequestType;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.PSOType;
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

    @Option(name = "-u", aliases = "--username", description = "Username", required = false, multiValued = false)
    String username;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        PSOIdentifierType psoUserId;
        if (username != null) {
            psoUserId = lookupUser(pspChannel, username).getPsoID();

        } else if (id != null) {
            psoUserId = new PSOIdentifierType();
            psoUserId.setTargetID(targetId);
            psoUserId.setID(id + "");
            psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
        } else {
            throw new IllegalArgumentException("Either id or username must be provided");
        }

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
        spmlRequest.setPsoID(psoUserId);

        return spmlRequest;
    }
    
}
