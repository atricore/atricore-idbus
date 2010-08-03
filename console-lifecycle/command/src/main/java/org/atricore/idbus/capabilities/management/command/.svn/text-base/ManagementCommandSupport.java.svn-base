package org.atricore.idbus.capabilities.management.command;

import org.apache.felix.karaf.shell.console.OsgiCommandSupport;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;
import org.osgi.framework.ServiceReference;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class ManagementCommandSupport extends OsgiCommandSupport {

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(IdentityApplianceManagementService.class.getName());
        if (ref == null) {
            System.out.println("Identity Appliance Management Service is unavailable. (no service reference)");
            return null;
        }
        try {
            IdentityApplianceManagementService svc = (IdentityApplianceManagementService) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("Identity Appliance Management Service service is unavailable. (no service)");
                return null;
            }

            doExecute(svc);

        } catch (IdentityServerException e) { // Force reference to exception class , do not change
            throw new RuntimeException(e.getMessage(), e);
            
        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;

    }



    protected abstract Object doExecute(IdentityApplianceManagementService svc) throws Exception;
}
