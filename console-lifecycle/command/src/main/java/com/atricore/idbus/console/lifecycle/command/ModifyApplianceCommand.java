package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.LookupIdentityApplianceByIdRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.UpdateIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.LookupIdentityApplianceByIdResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.UpdateIdentityApplianceResponse;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "appliance", name = "modify", description = "Modify Identity Appliance")
public class ModifyApplianceCommand extends ManagementCommandSupport {

    @Option(name = "-d", aliases = "--display-name", description = "Appliance Display Name", required = false, multiValued = false)
    String displayName;

    @Option(name = "-n", aliases = "--namespace", description = "Appliance Namespace", required = false, multiValued = false)
    String namespace;

    @Option(name = "--description", description = "Appliance Description ", required = false, multiValued = false)
    String description;

    @Argument(index = 0, name = "id", description = "The id of the identity appliance", required = true, multiValued = false)
    String id;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        LookupIdentityApplianceByIdRequest lkReq = new LookupIdentityApplianceByIdRequest ();
        lkReq.setIdentityApplianceId(id);

        LookupIdentityApplianceByIdResponse lkRes = svc.lookupIdentityApplianceById(lkReq);

        IdentityAppliance appliance = lkRes.getIdentityAppliance();
        IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

        if (displayName != null)
            applianceDef.setDisplayName(displayName);

        if (namespace != null)
            appliance.setNamespace(namespace);

        if (description != null)
            applianceDef.setDescription(description);

        UpdateIdentityApplianceRequest upReq = new UpdateIdentityApplianceRequest ();
        upReq.setAppliance(appliance);

        UpdateIdentityApplianceResponse upRes = svc.updateIdentityAppliance(upReq);

        appliance = upRes.getAppliance();

        cmdPrinter.print(appliance);

        return null;

    }
}
