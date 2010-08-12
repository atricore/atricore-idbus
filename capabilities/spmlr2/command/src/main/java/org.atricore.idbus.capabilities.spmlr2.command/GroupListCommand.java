package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.AddResponseType;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.PSOType;
import oasis.names.tc.spml._2._0.SelectionType;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.felix.gogo.commands.Argument;
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
@Command(scope = "spml", name = "groupls", description = "SPML Group Add operation")
public class GroupListCommand extends SpmlCommandSupport {

    @Argument(index = 2, name = "targetId", description = "Provisionig Service Target id", required = true)
    String targetId;

    @Override
    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setPath("Group/"); // All groups

        SearchQueryType spmlSearchQry = new SearchQueryType ();
        spmlSearchQry.setTargetID(targetId);
        spmlSearchQry.setScope(ScopeType.ONE_LEVEL);
        spmlSearchQry.getAny().add(spmlSelect);

        SearchRequestType spmlRequest = new SearchRequestType ();
        spmlRequest.setRequestID(idGen.generateId());
        spmlRequest.setQuery(spmlSearchQry);

        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);
        SearchResponseType res = (SearchResponseType) mediator.sendMessage(spmlRequest, ed, pspChannel);
        
        printGroups(res.getPso());
        
        return null;

    }
    
    protected void printGroups(List<PSOType> psoGroups) {
        
        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("  ID        Name           Description       \n");
        
        for (PSOType psoGroup : psoGroups) {
            psoGroup.getPsoID();
            GroupType spmlGroup = (GroupType) psoGroup.getData();
            sb.append("[");
            sb.append(getPsoIDString(psoGroup.getPsoID()));
            sb.append("]  [");
            sb.append(getGroupNameString(spmlGroup));
            sb.append("]    [");
            sb.append(getGroupDescriptionString(spmlGroup));
            sb.append("]    ");

            sb.append("\n");
        }

        System.out.println(sb);
    }
    
    protected String getPsoIDString(PSOIdentifierType psoId) {
        String id = psoId.getID();
        if (id == null)
            id = "--";

        while (id.length() < 12) {
            id = " " + id;
        }

        return id;
        
    }
    
    protected String getGroupNameString(GroupType spmlGroup) {
        String name = spmlGroup.getName();
        if (name == null)
            name = "--";
        
        while (name.length() < 18) {
            name += " ";
        }

        return name;
        
    }

    protected String getGroupDescriptionString(GroupType spmlGroup) {
        String description = spmlGroup.getName();
        if (description == null)
            description = "--";

        while (description.length() < 64) {
            description += " ";
        }

        return description;

    }


}
