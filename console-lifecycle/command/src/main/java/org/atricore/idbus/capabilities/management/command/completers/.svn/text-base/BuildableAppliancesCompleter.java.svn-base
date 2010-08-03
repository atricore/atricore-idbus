package org.atricore.idbus.capabilities.management.command.completers;

import org.apache.felix.karaf.shell.console.completer.StringsCompleter;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceState;
import org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.ListIdentityAppliancesByStateRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.ListIdentityAppliancesByStateResponse;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BuildableAppliancesCompleter extends OsgiCompleterSupport {

    @Override
    protected int complete(IdentityApplianceManagementService applianceMgrService, final String buffer, final int cursor, final List candidates) {

        StringsCompleter delegate = new StringsCompleter();

        try {
            ListIdentityAppliancesByStateRequest req = new ListIdentityAppliancesByStateRequest ();
            // TODO : List appliances that CAN be deployed ?
            req.setState(IdentityApplianceState.PROJECTED.toString());
            ListIdentityAppliancesByStateResponse res = applianceMgrService.listIdentityAppliancesByState(req);

            for (IdentityAppliance appliance : res.getAppliances()) {
                delegate.getStrings().add(appliance.getId() + "");
            }
        } catch (IdentityServerException e) {
            // Ignore
        }

        return delegate.complete(buffer, cursor, candidates);
    }

}
