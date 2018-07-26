package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.ListTargetsRequestType;
import oasis.names.tc.spml._2._0.RequestType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "targetsls", description = "SPML List Provisioning Service Targets operation")
public class ListTargetsCommand extends SpmlCommandSupport {

    @Option(name = "-p", aliases = "--profile", description = "SPML Profile (dsml/xsd) ", required = false, multiValued = false)
    String profile;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) {
        ListTargetsRequestType spmlRequest = new ListTargetsRequestType();

        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.targetAttr, "true");
        spmlRequest.setProfile(profile);

        return spmlRequest;
    }


}
