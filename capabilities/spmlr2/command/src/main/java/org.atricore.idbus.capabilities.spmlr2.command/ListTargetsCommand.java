package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "targetsls", description = "SPML List Provisioning Service Targets operation")
public class ListTargetsCommand extends SpmlCommandSupport {

    @Option(name = "-p", aliases = "--profile", description = "SPML Profile (dsml/xsd) ", required = false, multiValued = false)
    String profile;

    @Override
    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();

        ListTargetsRequestType req = new ListTargetsRequestType();
        req.setRequestID(idGen.generateId());
        req.setProfile(profile);

        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);

        ListTargetsResponseType response = (ListTargetsResponseType) mediator.sendMessage(req, ed, pspChannel);
        List<TargetType> targets = response.getTarget();
        if (targets == null || targets.size() == 0)
            throw new Exception("No targets found in PSP " + psp.getName());

        printTargets(targets);

        return null;
    }

    protected void printTargets(List<TargetType> targets) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("  ID        Profile           Capabilities       \n");
        for (TargetType target : targets) {

            // TODO : Build a line, using proper format and information (id, description, state, version, ... ?).
            // TODO : padd ids and states!
            sb.append("[");
            sb.append(getIdString(target));
            sb.append("]  [");
            sb.append(getProfileString(target));
            sb.append("]    [");
            sb.append(getCapabilitiesString(target));
            sb.append("]    ");

            sb.append("\n");

        }

        System.out.println(sb);

    }

    protected String getIdString(TargetType target) {
        String id = target.getTargetID();
        if (id == null)
            id = "--";

        while (id.length() < 12) {
            id = " " + id;
        }

        return id;
    }

    protected String getProfileString(TargetType target) {
        String p = target.getProfile();
        if (p == null)
            p = "";

        while (p.length() < 4) {
            p = p + " ";
        }

        return p;
    }

    protected String getCapabilitiesString(TargetType target) {
        // TODO : Implement me
        if (target.getCapabilities() == null)
            return "--";

        CapabilitiesListType capabilitiesList = target.getCapabilities();

        List<CapabilityType>  capabilities = capabilitiesList.getCapability();
        if (capabilities == null)
            return "--";

        StringBuffer sb = new StringBuffer();

        for (CapabilityType capability : capabilities) {
            sb.append(capability.getNamespaceURI());
            sb.append(",");
        }

        return sb.toString();
    }

}
