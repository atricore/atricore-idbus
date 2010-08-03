package org.atricore.idbus.capabilities.management.command.completers;

import org.apache.felix.karaf.shell.console.Completer;
import org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.ListIdentityAppliancesByStateRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class OsgiCompleterSupport implements Completer, org.apache.felix.karaf.shell.console.BundleContextAware {

    private BundleContext bundleContext;

    private IdentityApplianceManagementService idApplianceService;

    public IdentityApplianceManagementService getIdApplianceService() {
        return idApplianceService;
    }

    public void setIdApplianceService(IdentityApplianceManagementService idApplianceService) {
        this.idApplianceService = idApplianceService;
    }

    public int complete(String s, int i, List<String> strings) {

        if (idApplianceService != null) {
            return complete(idApplianceService, s, i, strings);
        }

        // The following code may not work !
        
        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(IdentityApplianceManagementService.class.getName());
        if (ref == null) {
            System.out.println("Identity Appliance Management Service is unavailable. (no service reference)");
            return 0;
        }

        try {
            IdentityApplianceManagementService svc = (IdentityApplianceManagementService) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("Identity Appliance Management Service service is unavailable. (no service)");
                return 0;
            }

            return complete(svc, s, i, strings);

        } finally {
            getBundleContext().ungetService(ref);
        }

    }

    protected abstract int complete(IdentityApplianceManagementService svc, final String buffer, final int cursor, final List candidates);

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}
