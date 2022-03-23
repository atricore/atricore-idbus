package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.DeleteRequestType;
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
@Command(scope = "spml", name = "usrdelete", description = "SPML User DELETE operation")
public class UserDeleteCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "User ID", required = false, multiValued = false)
    Long id;

    @Option(name = "-u", aliases = "--username", description = "Username", required = false, multiValued = false)
    String username;


    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        PSOIdentifierType psoId = null;
        if (username != null) {
            psoId = lookupUser(pspChannel, username).getPsoID();
        } else if (id != null) {
            psoId = new PSOIdentifierType ();
            psoId.setID(id + "");
            psoId.setTargetID(targetId);
        } else {
            throw new IllegalArgumentException("Either id or username must be provided");
        }

        DeleteRequestType spmlRequest = new DeleteRequestType ();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
        spmlRequest.setPsoID(psoId);
        
        return spmlRequest;


    }
}
